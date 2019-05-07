package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.model.Branch;
import com.sabinhantu.vcs.model.Commit;
import com.sabinhantu.vcs.model.DBFile;
import com.sabinhantu.vcs.model.DeltaSimulate;
import com.sabinhantu.vcs.repository.BranchRepository;
import com.sabinhantu.vcs.repository.CommitRepository;
import com.sabinhantu.vcs.repository.DBFileRepository;
import com.sabinhantu.vcs.repository.DeltaSimulateRepository;
import com.sabinhantu.vcs.service.DBFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Iterator;

@Controller
public class FileVersionController {
    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private CommitController commitController;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BranchController branchController;

    @Autowired
    private DBFileStorageService dbFileStorageService;

    @Autowired
    private DBFileRepository dbFileRepository;

    @Autowired
    private DeltaSimulateRepository deltaSimulateRepository;


    @GetMapping("/{username}/{projectUrl}/{branchName}/{fileIdString}/{currCommitIdString}/getprevious")
    public String fileToPreviousVersion(@PathVariable final String username,
                                        @PathVariable final String projectUrl,
                                        @PathVariable final String branchName,
                                        @PathVariable final String fileIdString,
                                        @PathVariable final String currCommitIdString) {
        Branch branch = branchController.getCurrentBranch(username, projectUrl, branchName);

        Long currentCommitId = Long.parseLong(currCommitIdString);
        Commit currentCommit = commitRepository.getOne(currentCommitId);

        Long fileId = Long.parseLong(fileIdString);
        DBFile file = dbFileStorageService.getFile(fileId);

        int currentCommitIndex = file.getCommits().headSet(currentCommit).size();

        // avoid ConcurrentModificationException
        for (int i = 0; i < currentCommitIndex; i++) {
            // remove deltasimulate from commit.deltaSimulateSet and from db
            Commit lastCommit = file.getLastCommit();
            deleteDeltasOfFileFromInsideCommit(lastCommit, fileId);

            // remove commit from file.commits
            file.getCommits().remove(file.getLastCommit());

            // if lastCommit.deltaSimulateSet remains empty => delete from branch and from db
            if (lastCommit.getDeltaSimulateSet().isEmpty()) {
                branch.removeCommit(lastCommit);
                branchRepository.save(branch);
                commitRepository.deleteById(lastCommit.getId());
            }
        }
        String constructStringCurrCommit = commitController.
                constructStringDataForCurrentCommit(file.getCommits(), currentCommit, file);
        file.setData(constructStringCurrCommit.getBytes());
        dbFileRepository.save(file);
        return "redirect:/" + username + "/" + projectUrl + "/" + branchName + "/file/" + file.getFileName();
    }


    @GetMapping("/{username}/{projectUrl}/{branchName}/{fileIdString}/deletefile")
    public String deleteFileFromBranch(@PathVariable final String username,
                                       @PathVariable final String projectUrl,
                                       @PathVariable final String branchName,
                                       @PathVariable final String fileIdString) {
        Branch branch = branchController.getCurrentBranch(username, projectUrl, branchName);
        Long fileId = Long.parseLong(fileIdString);
        DBFile file = dbFileStorageService.getFile(fileId);

        // remove file from branch.files and branch from dbfile.branches
        branch.removeFile(file);
        branchRepository.save(branch);

//        // remove deltas of current file from branch's commits
//        for (Commit commit : branch.getCommits()) {
//            deleteDeltasOfFileFromInsideCommit(commit, fileId);
//
//            // remove commit from dbfile.commits
//            if (dbfileContainsCommitId(file, commit.getId())) {
//                file.getCommits().remove(commit);
//            }
//            dbFileRepository.save(file);
//
//            // if commit.deltaSimulateSet is empty => delete commit from branch and db
//            if (commit.getDeltaSimulateSet().isEmpty()) {
//                branch.removeCommit(commit);
//                branchRepository.save(branch);
//            }
//        }
        branchRepository.save(branch);

        return "redirect:/" + username + "/" + projectUrl + "/tree/" + branchName;
    }

    protected void deleteDeltasOfFileFromInsideCommit(Commit commit, Long fileId) {
        Iterator<DeltaSimulate> itDelta = commit.getDeltaSimulateSet().iterator();
        while (itDelta.hasNext()) {
            DeltaSimulate deltaSimulate = itDelta.next();
            if (deltaSimulate.getFile().getId().equals(fileId)) {
                itDelta.remove();
                deltaSimulateRepository.deleteById(deltaSimulate.getId());
            }
        }
        commitRepository.save(commit);
    }
}
