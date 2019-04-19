package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.model.Branch;
import com.sabinhantu.vcs.model.Project;
import com.sabinhantu.vcs.model.User;
import com.sabinhantu.vcs.repository.ProjectRepository;
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


    //TODO: MERGE CUSTOM BRANCH TO MASTER


    @GetMapping("/{username}/{projectUrl}/tree/master")
    public String toMasterBranch(@PathVariable final String username,
                                 @PathVariable final String projectUrl) {
        return "redirect:/" + username + "/" + projectUrl;
    }

    //TODO:?????????????????
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
//                    Set<Commit> commits = branch.getCommits();
//                    model.addAttribute("commits", commits);
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
        project.addBranchWithName(branchForm.getName());
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
        //run only if the branch doesn't exist
        return "redirect:/" + username + "/" + projectUrl + "/settings?branchnotexist";
    }

    private boolean checkBranchAvailable(Project project, String branchName) {
        Set<Branch> branches = project.getBranches();
        for (Branch branch : branches) {
            if (branch.getName().equals(branchName))
                return false;
        }
        return true;
    }

}
