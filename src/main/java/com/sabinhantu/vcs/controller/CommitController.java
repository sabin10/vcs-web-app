package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.form.CommitForm;
import com.sabinhantu.vcs.model.*;
import com.sabinhantu.vcs.repository.*;
import com.sabinhantu.vcs.service.DBFileStorageService;
import com.sabinhantu.vcs.service.UserService;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    @GetMapping("/{username}/{projectUrl}/commits/{branchName}")
    public String toCommitsDetails(@PathVariable final String username,
                                   @PathVariable final String projectUrl,
                                   @PathVariable final String branchName,
                                   Model model) {
        Branch currentBranch = getCurrentBranch(username, projectUrl, branchName);
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

        Project project = projectRepository.findByUrl(projectUrl);
        Branch currentBranch = getCurrentBranch(username, projectUrl, branchName);


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

            /** if file already exist in branch and was changed **/
            if (doesFileExistInCurrentBranch(currentBranch, file)) {
                DBFile dbFile = dbFileRepository.getOne(getFileId(project, currentBranch, fileName));
                String stringFromOriginal = new String(dbFile.getData());
                String stringFromNew = null;
                try {
                    // update file's data and last commit
                    dbFile.setData(file.getBytes());
                    dbFile.setLastCommit(newCommit);
                    dbFileRepository.save(dbFile);
                    stringFromNew = new String(file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // create patch
                Patch patch = getPatch(stringFromOriginal, stringFromNew);
                // deltas create and save to DeltaSimulate Entity
                for (Delta delta : patch.getDeltas()) {
                    DeltaSimulate deltaSimulate = transformDeltaInDeltaSimulate(delta);
                    deltaSimulate.setFile(dbFile);
                    deltaSimulateRepository.save(deltaSimulate);
                    newCommit.addDeltaSimulate(deltaSimulate);
                }
                commitRepository.save(newCommit);
            } else {
                /** when file is not already in branch **/
                dbFileStorageService.storeFile(file);
                // get last uploaded file's id
                long countFilesRepository = dbFileRepository.count();

                DBFile dbFile = dbFileRepository.getOne(countFilesRepository);
                dbFile.setLastCommit(newCommit);
                dbFileRepository.save(dbFile);
                currentBranch.addFile(dbFile);

                String stringFromFile = new String(dbFile.getData());
                Patch patch = getPatch("", stringFromFile);

                // first upload has only one delta
                DeltaSimulate deltaSimulate = transformDeltaInDeltaSimulate(patch.getDeltas().get(0));
                deltaSimulate.setFile(dbFile);
                deltaSimulateRepository.save(deltaSimulate);
                newCommit.addDeltaSimulate(deltaSimulate);
                commitRepository.save(newCommit);
            }
        }
        currentBranch.addCommit(newCommit);
        branchRepository.save(currentBranch);

        return "redirect:/" + username + "/" + projectUrl + "/tree/" + branchName;
    }

    @GetMapping("/{username}/{projectUrl}/{branchName}/commit/{commitIdString}")
    public String getCommitDetails(@PathVariable final String username,
                                   @PathVariable final String projectUrl,
                                   @PathVariable final String branchName,
                                   @PathVariable final String commitIdString,
                                   Model model) {
        Long commitId = Long.parseLong(commitIdString);
        Commit commit = getCurrentCommit(getCurrentBranch(username, projectUrl, branchName), commitId);
        if (commit == null) {
            return "error";
        }
        model.addAttribute("commit", commit);
        model.addAttribute("deltas", commit.getDeltaSimulateSet());

//        List<FileForm> filesForm = new ArrayList<>();
//        Set<DBFile> dbFiles = commit.getFiles();
//        for (DBFile dbFile : dbFiles) {
//            filesForm.add(new FileForm(dbFile.getFileName(), new String(dbFile.getData())));
//        }
//        model.addAttribute("filesForm", filesForm);

        return "commitdetails";
    }

    private List<?> stringToLinesList(String str) {
        String[] arr = str.split("\n");
        List<?> list = Arrays.asList(arr);
        return list;
    }

    protected Branch getCurrentBranch(String username, String projectUrl, String branchName) {
        User user = userService.findByUsername(username);
        Set<Project> projects = user.getProjects();
        for (Project project : projects) {
            if (project.getUrl().equals(projectUrl)) {
                Set<Branch> branches = project.getBranches();
                for (Branch branch : branches) {
                    if (branch.getName().equals(branchName)) {
                        return branch;
                    }
                }
            }
        }
        return null;
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

    protected DeltaSimulate transformDeltaInDeltaSimulate(Delta delta) {
        String deltaType = delta.getType().toString();
        int positionOrig = delta.getOriginal().getPosition();
        String linesOrig = linesToString(delta.getOriginal().getLines());

        int positionRevis = delta.getRevised().getPosition();
        String linesRevis = linesToString(delta.getRevised().getLines());
        return new DeltaSimulate(deltaType, positionOrig, linesOrig, positionRevis, linesRevis);
    }

}
