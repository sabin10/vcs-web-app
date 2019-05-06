package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.form.CommitForm;
import com.sabinhantu.vcs.form.FileForm;
import com.sabinhantu.vcs.model.*;
import com.sabinhantu.vcs.repository.*;
import com.sabinhantu.vcs.service.DBFileStorageService;
import com.sabinhantu.vcs.service.UserService;
import difflib.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@Controller
public class CommitController {
    @Autowired
    private UserService userService;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private DBFileStorageService dbFileStorageService;

    @Autowired
    private DBFileRepository dbFileRepository;

    @Autowired
    private DeltaSimulateRepository deltaSimulateRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BranchController branchController;

    @GetMapping("/{username}/{projectUrl}/commits/{branchName}")
    public String toCommitsDetails(@PathVariable final String username,
                                   @PathVariable final String projectUrl,
                                   @PathVariable final String branchName,
                                   Model model) {
        Branch currentBranch = branchController.getCurrentBranch(username, projectUrl, branchName);
        Set<Commit> commits = currentBranch.getCommits();
        model.addAttribute("commits", commits);
        model.addAttribute("username", username);
        model.addAttribute("projectUrl", projectUrl);
        model.addAttribute("branchName", branchName);
        return "commits";
    }

    @GetMapping("/{username}/{projectUrl}/{branchName}/addcommit")
    public String getAddCommitForm(@PathVariable final String username,
                                   @PathVariable final String projectUrl,
                                   @PathVariable final String branchName,
                                   Model model) {
        if (!doesBranchExist(username, projectUrl, branchName)) {
            return "error";
        }
        model.addAttribute("username", username);
        model.addAttribute("projectUrl", projectUrl);
        model.addAttribute("branchName", branchName);
        return "addcommit";
    }

    @PostMapping("/{username}/{projectUrl}/{branchName}/addcommit")
    public String postAddCommit(@PathVariable final String username,
                                @PathVariable final String projectUrl,
                                @PathVariable final String branchName,
                                @ModelAttribute("commitForm") @Valid CommitForm commitForm,
                                @RequestParam("files") MultipartFile[] files) {
        Commit newCommit = new Commit(commitForm.getName(), commitForm.getDescription());
        User userLogged = userService.findByUsername(AccountController.loggedInUsername());
        newCommit.setCreator(userLogged);
        commitRepository.save(newCommit);
        boolean commitMadeChanges = false;

        Project project = projectRepository.findByUrl(projectUrl);
        Branch currentBranch = branchController.getCurrentBranch(username, projectUrl, branchName);


        for (MultipartFile file : files) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            /** if no changes occured to a file, algorithm skips this file **/
            try {
                if (newFileEqualsOldFile(project, currentBranch, file)) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            commitMadeChanges = true;

            Patch patch = null;
            DBFile dbFile = null;
            /** if file already exist in branch and was changed **/
            if (doesFileExistInCurrentBranch(currentBranch, file)) {
                dbFile = dbFileRepository.getOne(getFileId(project, currentBranch, fileName));
                String stringFromOriginal = new String(dbFile.getData());
                String stringFromNew = null;
                try {
                    // update file's data and last commit
                    dbFile.setData(file.getBytes());
                    //dbFile.setLastCommit(newCommit);
                    dbFile.getCommits().add(newCommit);
                    dbFileRepository.save(dbFile);
                    stringFromNew = new String(file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // create patch
                patch = getPatch(stringFromOriginal, stringFromNew);
            } else {
                /** when file is not already in branch **/
                dbFileStorageService.storeFile(file);
                // get last uploaded file's id
                long countFilesRepository = dbFileRepository.count();

                dbFile = dbFileRepository.getOne(countFilesRepository);
                //dbFile.setLastCommit(newCommit);
                dbFile.getCommits().add(newCommit);
                dbFileRepository.save(dbFile);
                currentBranch.addFile(dbFile);

                String stringFromFile = new String(dbFile.getData());
                patch = getPatch("", stringFromFile);

            }
            // create deltas and save to DB
            for (Delta delta : patch.getDeltas()) {
                DeltaSimulate deltaSimulate = transformDeltaInDeltaSimulate(delta);
                deltaSimulate.setFile(dbFile);
                deltaSimulateRepository.save(deltaSimulate);
                newCommit.addDeltaSimulate(deltaSimulate);
            }
            commitRepository.save(newCommit);
        }
        if (commitMadeChanges) {
            currentBranch.addCommit(newCommit);
            branchRepository.save(currentBranch);
        } else {
            commitRepository.delete(newCommit);
        }

        return "redirect:/" + username + "/" + projectUrl + "/tree/" + branchName;
    }

    @GetMapping("/{username}/{projectUrl}/{branchName}/commit/{commitIdString}")
    public String getCommitDetails(@PathVariable final String username,
                                   @PathVariable final String projectUrl,
                                   @PathVariable final String branchName,
                                   @PathVariable final String commitIdString,
                                   Model model) {
        Long commitId = Long.parseLong(commitIdString);
        Commit currentCommit = getCurrentCommit(branchController.getCurrentBranch(username, projectUrl, branchName), commitId);
        if (currentCommit == null) {
            return "error";
        }
        model.addAttribute("commit", currentCommit);

        // get files updated in current commit
        SortedSet<DBFile> filesUpdated = new TreeSet<>();
        for (DeltaSimulate deltaSimulate : currentCommit.getDeltaSimulateSet()) {
            filesUpdated.add(deltaSimulate.getFile());
        }

        // list of files for view
        List<FileForm> fileForms = new ArrayList<>();

        for (DBFile fileUpdated: filesUpdated) {
            // reverse set of commits for reconstructing data from first commit until current
            SortedSet<Commit> commits = new TreeSet<>(Collections.reverseOrder());
            commits.addAll(fileUpdated.getCommits());
            String dataStringFile = constructStringDataForCurrentCommit(commits, currentCommit, fileUpdated);
            fileForms.add(new FileForm(fileUpdated.getFileName(), dataStringFile, fileUpdated.getId()));
        }

        model.addAttribute("fileForms", fileForms);
        model.addAttribute("username", username);
        model.addAttribute("projectUrl", projectUrl);
        model.addAttribute("branchName", branchName);

        return "commitdetails";
    }

    protected boolean doesFileExistInCurrentBranch(Branch currentBranch, MultipartFile newFile) {
        String fileName = StringUtils.cleanPath(newFile.getOriginalFilename());
        for (DBFile file : currentBranch.getFiles()) {
            if (file.getFileName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    protected boolean newFileEqualsOldFile(Project project, Branch currentBranch, MultipartFile newFile) throws IOException {
        String fileName = StringUtils.cleanPath(newFile.getOriginalFilename());
        if (doesFileExistInCurrentBranch(currentBranch, newFile)) {
            // check if data is the same
            DBFile file = dbFileRepository.getOne(getFileId(project, currentBranch, fileName));
            String originalString = new String(file.getData());
            String revisedString = new String(newFile.getBytes());
            if (originalString.equals(revisedString)) {
                return true;
            } else {
                return false;
            }

        }
        return false;
    }

    protected Long getFileId(Project project, Branch branch, String fileName) {
        for (Branch br : project.getBranches()) {
            if (br.getId().equals(branch.getId())) {
                for (DBFile file : br.getFiles()) {
                    if (file.getFileName().equals(fileName)) {
                        return file.getId();
                    }
                }
            }
        }
        return null;
    }

    protected Commit getCurrentCommit(Branch branch, Long commitId) {
        Set<Commit> commits = branch.getCommits();
        for (Commit commit : commits) {
            if (commit.getId().equals(commitId)) {
                return commit;
            }
        }
        return null;
    }

    protected boolean doesBranchExist(String username, String projectUrl, String branchName) {
        User user = userService.findByUsername(username);
        Set<Project> projects = user.getProjects();
        for (Project project : projects) {
            if (project.getUrl().equals(projectUrl)) {
                Set<Branch> branches = project.getBranches();
                for (Branch branch : branches) {
                    if (branch.getName().equals(branchName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // java-diff-utils

    protected Patch getPatch(String original, String revised) {
        List<String> originalList = stringToListOfStrings(original);
        List<String> revisedList  = stringToListOfStrings(revised);

        // diff parameters are lists of strings
        Patch patch = DiffUtils.diff(originalList, revisedList);
        return patch;
    }

    protected String getDiff(String originalDataString, Patch patch) {
        List<String> original = stringToListOfStrings(originalDataString);

        try {
            List<String> result = (List<String>) patch.applyTo(original);
            StringBuilder stringList = new StringBuilder();

            for(int i = 0; i < result.size(); i++) {
                String s = result.get(i);
                if(i != result.size() - 1)
                    stringList.append(s + "\n");
                else
                    stringList.append(s);
            }

            String merge = String.valueOf(stringList);
            return merge;
        } catch (PatchFailedException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected List<String> stringToListOfStrings(String str) {
        String[] arr = str.split("\n");
        List<String> list = Arrays.asList(arr);
        return list;
    }

    protected String linesToString(List<?> lines) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            res.append(lines.get(i));
            res.append("\n");
        }
        return res.toString();
    }

    protected List<?> stringToLinesList(String str) {
        String[] arr = str.split("\n");
        List<?> list = Arrays.asList(arr);
        return list;
    }

    protected DeltaSimulate transformDeltaInDeltaSimulate(Delta delta) {
        String deltaType = delta.getType().toString();
        int positionOrig = delta.getOriginal().getPosition();
        String linesOrig = linesToString(delta.getOriginal().getLines());

        int positionRevis = delta.getRevised().getPosition();
        String linesRevis = linesToString(delta.getRevised().getLines());
        return new DeltaSimulate(deltaType, positionOrig, linesOrig, positionRevis, linesRevis);
    }

    protected Delta transformSimulateInDelta(DeltaSimulate deltaSimulate) {
        Chunk originalChunk = new Chunk(deltaSimulate.getPositionOriginal(), stringToLinesList(deltaSimulate.getLinesOriginal()));
        Chunk revisedChunk = new Chunk(deltaSimulate.getPositionRevised(), stringToLinesList(deltaSimulate.getLinesRevised()));
        if (deltaSimulate.getDeltaType().equals("INSERT"))
            return new InsertDelta(originalChunk, revisedChunk);
        if (deltaSimulate.getDeltaType().equals("CHANGE"))
            return new ChangeDelta(originalChunk, revisedChunk);
        return new DeleteDelta(originalChunk, revisedChunk);
    }

    protected String constructStringDataForCurrentCommit(SortedSet<Commit> commits, Commit currentCommit, DBFile file) {
        // knows when to stop
        int currentCommitIndex = commits.headSet(currentCommit).size();
        int counter = 0;
        String resultData = "";
        for (Commit commit : commits) {
            // create patch
            Patch patch = new Patch();
            for (DeltaSimulate deltaSimulate : commit.getDeltaSimulateSet()) {
                if (deltaSimulate.getFile().getFileName().equals(file.getFileName())) {
                    Delta delta = transformSimulateInDelta(deltaSimulate);
                    patch.addDelta(delta);
                }
            }
            resultData = getDiff(resultData, patch);
            if (counter++ == currentCommitIndex) {
                break;
            }
        }
        return resultData;

    }

}
