package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.repository.RepositoryRepository;
import com.sabinhantu.vcs.repository.UserRepository;
import com.sabinhantu.vcs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AddRepositoryController {
    @Autowired
    private UserService userService;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/addrepository")
    public String moveToAddRepository() {
        String usernameLoggedIn = AccountController.loggedInUsername();
        return "redirect:/" + usernameLoggedIn + "/addrepository";
    }

    @GetMapping("/{username}/addrepository")
    public String showAddRepository(@PathVariable final String username) {
        return "addrepository";
    }
}
