package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.form.FileForm;
import com.sabinhantu.vcs.model.Branch;
import com.sabinhantu.vcs.model.Commit;
import com.sabinhantu.vcs.model.DBFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Controller
public class FileDisplayController {
    @Autowired
    private BranchController branchController;

    @Autowired
    private CommitController commitController;

    @GetMapping("/{username}/{projectUrl}/{branchName}/file/{fileName}")
    public String displayFile(@PathVariable final String username,
                              @PathVariable final String projectUrl,
                              @PathVariable final String branchName,
                              @PathVariable final String fileName,
                              Model model) {
        Branch currentBranch = branchController.getCurrentBranch(username, projectUrl, branchName);
        DBFile file = getFileFromBranch(currentBranch, fileName);
        FileForm fileForm = new FileForm(file.getFileName(), file.getStringData());
        Set<Commit> commits = file.getCommits();
        model.addAttribute("fileForm", fileForm);
        model.addAttribute("commits", commits);
        model.addAttribute("currentCommit", ((SortedSet<Commit>) commits).first());

        return "filedisplay";
    }

    @GetMapping("/{username}/{projectUrl}/{branchName}/{commitIdString}/file/{fileName}")
    public String displayFileFromCommit(@PathVariable final String username,
                                        @PathVariable final String projectUrl,
                                        @PathVariable final String branchName,
                                        @PathVariable final String commitIdString,
                                        @PathVariable final String fileName,
                                        Model model) {
        Long commitId = Long.parseLong(commitIdString);
        Branch currentBranch = branchController.getCurrentBranch(username, projectUrl, branchName);
        Commit currentCommit = commitController.getCurrentCommit(currentBranch, commitId);
        model.addAttribute("currentCommit", currentCommit);
        DBFile file = getFileFromBranch(currentBranch, fileName);
        Set<Commit> commits = file.getCommits();
        model.addAttribute("commits", commits);

        // reconstruct data until current commit
        SortedSet<Commit> commitsFromStart = new TreeSet<>(Collections.reverseOrder());
        commitsFromStart.addAll(commits);
        String dataStringFile = commitController.
                constructStringDataForCurrentCommit(commitsFromStart, currentCommit, file);
        FileForm fileForm = new FileForm(file.getFileName(), dataStringFile);
        model.addAttribute("fileForm", fileForm);
        return "filedisplay";
    }

    protected DBFile getFileFromBranch(Branch branch, String fileName) {
        Set<DBFile> files = branch.getFiles();
        for (DBFile file : files) {
            if (file.getFileName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

}
