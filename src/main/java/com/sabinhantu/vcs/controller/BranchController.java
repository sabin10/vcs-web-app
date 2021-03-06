package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.model.*;
import com.sabinhantu.vcs.repository.BranchRepository;
import com.sabinhantu.vcs.repository.ProjectRepository;
import com.sabinhantu.vcs.repository.UserRepository;
import com.sabinhantu.vcs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
public class BranchController {
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectController projectController;

    @Autowired
    private BranchRepository branchRepository;

    @GetMapping("/{username}/{projectUrl}/tree/master")
    public String toMasterBranch(@PathVariable final String username,
                                 @PathVariable final String projectUrl) {
        return "redirect:/" + username + "/" + projectUrl;
    }

    @GetMapping("/{username}/{projectUrl}/tree/{branchName}")
    public String toCustomBranch(@PathVariable final String username,
                                 @PathVariable final String projectUrl,
                                 @PathVariable final String branchName,
                                 Model model) {
        Project project = projectRepository.findByUrl(projectUrl);
        try {
            Set<Branch> branches = project.getBranches();
            for (Branch branch : branches) {
                if (branch.getName().equals(branchName)) {
                    String usernameLoggedIn = AccountController.loggedInUsername();
                    User userRequested = userService.findByUsername(username);
                    model.addAttribute("userRequested", userRequested);
                    model.addAttribute("usernameLoggedIn", usernameLoggedIn);
                    model.addAttribute("project", project);
                    model.addAttribute("branches", branches);
                    int commitsNumber = branch.getCommits().size();
                    model.addAttribute("commitsNumber", commitsNumber);
                    model.addAttribute("currentBranch", branch.getName());

                    // Retrieve files for current branch
                    Set<DBFile> files = getCurrentBranch(username, projectUrl, branchName).getFiles();
                    model.addAttribute("files", files);

                    // has custom branch changes from master branch ?
                    boolean newBranchHasChanges = projectController.
                            customBranchDiffFromMaster(branch, getMasterBranch(username, projectUrl));

                    model.addAttribute("newBranchHasChanges", newBranchHasChanges);

                    return "project";
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return "error";
    }

    @PostMapping("/{username}/{projectUrl}/addbranch")
    public String addBranch(@ModelAttribute("branchForm") Branch branchForm,
                            @PathVariable final String username,
                            @PathVariable final String projectUrl) {
        if (branchForm.getName().contains(" ")) {
            return "redirect:/" + username + "/" + projectUrl + "/settings?branchnameerror";
        }
        Project project = projectRepository.findByUrl(projectUrl);
        if (!checkBranchAvailable(project, branchForm.getName())) {
            return "redirect:/" + username + "/" + projectUrl + "/settings?branchexist";
        }

        // When new branch is created, it will continue master branch
        Branch newBranch = new Branch(branchForm.getName());
        Set<Commit> masterCommits = getMasterBranch(username, projectUrl).getCommits();
        // add files from master
        newBranch.getFiles().addAll(getMasterBranch(username, projectUrl).getFiles());
        newBranch.getCommits().addAll(masterCommits);
        project.getBranches().add(newBranch);
        projectRepository.save(project);
        return "redirect:/" + username + "/" + projectUrl + "/settings";
    }

    @PostMapping("/{username}/{projectUrl}/deletebranch")
    public String deleteBranch(@ModelAttribute("branchForm") Branch branchForm,
                               @PathVariable final String username,
                               @PathVariable final String projectUrl) {
        Project project = projectRepository.findByUrl(projectUrl);
        if (branchForm.getName().equals("master")) {
            return "redirect:/" + username + "/" + projectUrl + "/settings?branchmasterdeleteerror";
        }
        Set<Branch> projectBranches = project.getBranches();
        for (Branch branch : projectBranches) {
            if (branch.getName().equals(branchForm.getName())) {
                project.getBranches().remove(branch);
                projectRepository.save(project);
                return "redirect:/" + username + "/" + projectUrl + "/settings";
            }
        }
        return "redirect:/" + username + "/" + projectUrl + "/settings?branchnotexist";
    }

    /** MERGE CUSTOM BRANCH TO MASTER**/
    @GetMapping("/{username}/{projectUrl}/{currentBranchName}/mergetomaster")
    public String mergeBranchToMaster(@PathVariable final String username,
                                      @PathVariable final String projectUrl,
                                      @PathVariable final String currentBranchName) {
        Branch masterBranch = getMasterBranch(username, projectUrl);
        Branch currentBranch = getCurrentBranch(username, projectUrl, currentBranchName);

        // add to master commits surplus from current
        for (Commit commit : currentBranch.getCommits()) {
            if (!masterBranch.getCommits().contains(commit)) {
                masterBranch.addCommit(commit);
            }
        }
        branchRepository.save(masterBranch);

        // add to master files from current
        for (DBFile dbFile : currentBranch.getFiles()) {
            if (!masterBranch.getFiles().contains(dbFile)) {
                masterBranch.addFile(dbFile);
            }
        }
        branchRepository.save(masterBranch);

        return "redirect:/" + username + "/" + projectUrl;
    }

    protected Branch getMasterBranch(String username, String projectUrl) {
        User user = userRepository.findByUsername(username);
        Set<Project> projects = user.getProjects();
        for (Project project : projects) {
            if (project.getUrl().equals(projectUrl)) {
                Set<Branch> branches = project.getBranches();
                for (Branch branch : branches) {
                    if (branch.getName().equals("master"))
                        return branch;
                }
            }
        }
        return null;
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

    protected boolean checkBranchAvailable(Project project, String branchName) {
        Set<Branch> branches = project.getBranches();
        for (Branch branch : branches) {
            if (branch.getName().equals(branchName))
                return false;
        }
        return true;
    }
}
