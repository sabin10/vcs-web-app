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

@Controller
public class AddProjectController {
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/addproject")
    public String moveToAddRepository() {
        String usernameLoggedIn = AccountController.loggedInUsername();
        return "redirect:/" + usernameLoggedIn + "/addproject";
    }

    @GetMapping("/{username}/addproject")
    public String showAddRepository(@PathVariable final String username,
                                    Model model) {
        String usernameLoggedIn = AccountController.loggedInUsername();
        model.addAttribute("username", usernameLoggedIn);
        return "addproject";
    }

    @PostMapping("/{usernameUrl}/addproject")
    public String addRepository(@PathVariable final String usernameUrl,
                                @ModelAttribute("repositoryForm") Project projectForm,
                                Model model) {
        Project newProject = new Project(projectForm.getTitle(), projectForm.getDescription());
        User currentUser = userService.findByUsername(usernameUrl);

        projectRepository.save(newProject);
        currentUser.addProject(newProject);
        userRepository.save(currentUser);

        return "redirect:/" + usernameUrl + "/" + newProject.getUrl();
    }
}
