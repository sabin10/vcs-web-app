package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.form.CommitForm;
import com.sabinhantu.vcs.model.*;
import com.sabinhantu.vcs.repository.BranchRepository;
import com.sabinhantu.vcs.repository.CommitRepository;
import com.sabinhantu.vcs.repository.DBFileRepository;
import com.sabinhantu.vcs.service.DBFileStorageService;
import com.sabinhantu.vcs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
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

    @GetMapping("/{username}/{projectUrl}/commits/{branchName}")
    public String toCommitsDetails(@PathVariable final String username,
                                   @PathVariable final String projectUrl,
                                   @PathVariable final String branchName,
                                   Model model) {
        Branch currentBranch = getCurrentBranch(username, projectUrl, branchName);
        Set<Commit> commits = currentBranch.getCommits();
        model.addAttribute("commits", commits);
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

        // List keep id for each file uploaded
        List<Long> filesIds = new ArrayList<>();
        for (MultipartFile file : files) {
            dbFileStorageService.storeFile(file);
            long countFilesRepository = dbFileRepository.count();
            filesIds.add(countFilesRepository);
        }

        // Adding files to the new commit
        for (Long fileId : filesIds) {
            DBFile dbFile = dbFileRepository.getOne(fileId);
            newCommit.addFile(dbFile);
            commitRepository.save(newCommit);
        }

        Branch currentBranch = getCurrentBranch(username, projectUrl, branchName);
        currentBranch.addCommit(newCommit);
        branchRepository.save(currentBranch);
        return "redirect:/" + username + "/" + projectUrl + "/tree/" + branchName;
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

}
