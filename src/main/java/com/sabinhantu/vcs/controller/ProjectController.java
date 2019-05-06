package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.model.Branch;
import com.sabinhantu.vcs.model.DBFile;
import com.sabinhantu.vcs.model.Project;
import com.sabinhantu.vcs.model.User;
import com.sabinhantu.vcs.repository.ProjectRepository;
import com.sabinhantu.vcs.repository.UserRepository;
import com.sabinhantu.vcs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Set;

@Controller
public class ProjectController {
    @Autowired
    private  UserService userService;

    @Autowired
    private  ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchController branchController;


    @GetMapping("/{username}/{projectUrl}")
    public String userRepository(@PathVariable final String username,
                                 @PathVariable final String projectUrl,
                                 Model model) {
        User userRequested = userService.findByUsername(username);
        Project projectRequested = projectRepository.findByUrl(projectUrl);
        if (!doesRepositoryExist(username, projectUrl) || !userOwnsRepository(userRequested, projectRequested)) {
            return "error";
        }
        Set<Branch> branches = projectRequested.getBranches();
        for (Branch branch : branches) {
            if (branch.getName().equals("master")) {
                int commitsNumber = branch.getCommits().size();
                model.addAttribute("commitsNumber", commitsNumber);
            }
        }
        String usernameLoggedIn = AccountController.loggedInUsername();
        model.addAttribute("userRequested", userRequested);
        model.addAttribute("usernameLoggedIn", usernameLoggedIn);
        model.addAttribute("project", projectRequested);
        model.addAttribute("branches", branches);
        model.addAttribute("currentBranch", "master");

        // Retrieve files from db
        Set<DBFile> files = branchController.getMasterBranch(username, projectUrl).getFiles();
        model.addAttribute("files", files);

        return "project";
    }

    @GetMapping("/{username}/{projectUrl}/settings")
    public String repositorySettings(@PathVariable final String username,
                                     @PathVariable final String projectUrl,
                                     Model model) {
        if (!doesRepositoryExist(username, projectUrl) || !username.equals(AccountController.loggedInUsername())) {
            return "error";
        }
        User userOwner = userService.findByUsername(username);
        List<User> usersOwners = userRepository.findByProjects_Url(projectUrl);
        Project projectRequested = projectRepository.findByUrl(projectUrl);
        Set<Branch> branches = projectRequested.getBranches();
        model.addAttribute("usersOwners", usersOwners);
        model.addAttribute("userOwner", userOwner);
        model.addAttribute("project", projectRequested);
        model.addAttribute("branches", branches);
        return "projectsettings";
    }

//    protected Project getProjectByUrlAndUser(String username, String projectUrl) {
//        User user = userService.findByUsername(username);
//        for (Project project : user.getProjects()) {
//            if (project.getUrl().equals(projectUrl)) {
//                return project;
//            }
//        }
//        return null;
//    }

    protected boolean doesRepositoryExist(String username, String repositoryUrl) {
        User userRequested = userService.findByUsername(username);
        Project projectRequested = projectRepository.findByUrl(repositoryUrl);
        if (userRequested == null || projectRequested == null) {
            return false;
        }
        return true;
    }

    protected boolean userOwnsRepository(User user, Project project) {
        if (user.getProjects().contains(project))
            return true;
        return false;
    }
}