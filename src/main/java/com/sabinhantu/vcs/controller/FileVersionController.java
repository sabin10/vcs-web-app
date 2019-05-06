package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.model.Commit;
import com.sabinhantu.vcs.model.DBFile;
import com.sabinhantu.vcs.repository.CommitRepository;
import com.sabinhantu.vcs.repository.DBFileRepository;
import com.sabinhantu.vcs.repository.ProjectRepository;
import com.sabinhantu.vcs.service.DBFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FileVersionController {
    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private CommitController commitController;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BranchController branchController;

    @Autowired
    private DBFileStorageService dbFileStorageService;

    @Autowired
    private DBFileRepository dbFileRepository;


    @GetMapping("/{username}/{projectUrl}/{branchName}/{fileIdString}/{currCommitIdString}/getprevious")
    public String fileToPreviousVersion(@PathVariable final String username,
                                        @PathVariable final String projectUrl,
                                        @PathVariable final String branchName,
                                        @PathVariable final String fileIdString,
                                        @PathVariable final String currCommitIdString) {
//        Project project = projectRepository.findByUrl(projectUrl);
//        Commit currentCommit = commitRepository.getOne(currentCommitId);
//        Branch branch = null;
//        try {
//            branch = branchController.getCurrentBranch(username, projectUrl, branchName);
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//            return "error";
//        }
        Long currentCommitId = Long.parseLong(currCommitIdString);
        Commit currentCommit = commitRepository.getOne(currentCommitId);

        Long fileId = Long.parseLong(fileIdString);
        DBFile file = dbFileStorageService.getFile(fileId);

        int currentCommitIndex = file.getCommits().headSet(currentCommit).size();
        int currIndex = 0;


        // avoid ConcurrentModificationException
        for (int i = 0; i < currentCommitIndex; i++) {
            file.getCommits().remove(file.getLastCommit());
        }

        String constructStringCurrCommit = commitController.
                constructStringDataForCurrentCommit(file.getCommits(), currentCommit, file);
        file.setData(constructStringCurrCommit.getBytes());
        dbFileRepository.save(file);


        return "redirect:/" + username + "/" + projectUrl + "/" + branchName + "/file/" + file.getFileName();
    }
}
