package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.model.Project;
import com.sabinhantu.vcs.model.User;
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

import java.util.List;

@Controller
public class ProjectController {
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{username}/{projectUrl}")
    public String userRepository(@PathVariable final String username,
                                 @PathVariable final String projectUrl,
                                 Model model) {
        User userRequested = userService.findByUsername(username);
        Project projectRequested = projectRepository.findByUrl(projectUrl);
        if (!doesRepositoryExist(username, projectUrl) || !userOwnsRepository(userRequested, projectRequested)) {
            return "error";
        }
        List<User> usersOwners = userRepository.findByProjects_Url(projectUrl);
        String usernameLoggedIn = AccountController.loggedInUsername();
        model.addAttribute("userRequested", userRequested);
        model.addAttribute("usernameLoggedIn", usernameLoggedIn);
        model.addAttribute("project", projectRequested);
        model.addAttribute("usersOwners", usersOwners);
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
        Project projectRequested = projectRepository.findByUrl(projectUrl);
        model.addAttribute("userOwner", userOwner);
        model.addAttribute("project", projectRequested);

        return "projectsettings";
    }


    //post add member ownership to repository
    @PostMapping("/{usernameUrl}/{projectUrl}/addmember")
    public String addMemberOwnershipToRepository(@PathVariable final String usernameUrl,
                                                 @PathVariable final String projectUrl,
                                                 @ModelAttribute("userForm") User userForm,
                                                 Model model) {
        if (!usernameUrl.equals(AccountController.loggedInUsername())){
            return "error";
        }
        User newMemberUser = userService.findByUsername(userForm.getUsername());
        Project project = projectRepository.findByUrl(projectUrl);

        if (newMemberUser == null || userOwnsRepository(newMemberUser, project)) {
            return "redirect:/" + usernameUrl + "/" + projectUrl + "/settings?error";
        }

        newMemberUser.addProject(project);
        userRepository.save(newMemberUser);

        return "redirect:/" + usernameUrl + "/" + projectUrl;
    }


    // TODO: Functie asa sau throw exception? intreaba Karla
    private boolean doesRepositoryExist(String username, String repositoryUrl) {
        User userRequested = userService.findByUsername(username);
        Project projectRequested = projectRepository.findByUrl(repositoryUrl);
        if (userRequested == null || projectRequested == null) {
            return false;
        }
        return true;
    }

    private boolean userOwnsRepository(User user, Project project) {
        if (user.getProjects().contains(project))
            return true;
        return false;
    }
}
