package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.model.Branch;
import com.sabinhantu.vcs.model.Commit;
import com.sabinhantu.vcs.model.Project;
import com.sabinhantu.vcs.model.User;
import com.sabinhantu.vcs.repository.ProjectRepository;
import com.sabinhantu.vcs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@Controller
public class CommitController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/{username}/{projectUrl}/commits/{branchName}")
    public String toCommitsDetails(@PathVariable final String username,
                                   @PathVariable final String projectUrl,
                                   @PathVariable final String branchName,
                                   Model model) {
        User user = userService.findByUsername(username);
        Set<Project> projects = user.getProjects();
        for (Project project : projects) {
            if (project.getUrl().equals(projectUrl)) {
                Set<Branch> branches = project.getBranches();
                for (Branch branch : branches) {
                    if (branch.getName().equals(branchName)) {
                        Set<Commit> commits = branch.getCommits();
                        model.addAttribute("commits", commits);
                        return "commits";
                    }
                }
            }
        }

        return "error";
    }
}
