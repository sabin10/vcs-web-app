package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.model.Repository;
import com.sabinhantu.vcs.model.User;
import com.sabinhantu.vcs.repository.RepositoryRepository;
import com.sabinhantu.vcs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RepositoryController {
    @Autowired
    private UserService userService;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @GetMapping("/{username}/{repositoryUrl}")
    public String userRepository(@PathVariable final String username,
                                 @PathVariable final String repositoryUrl,
                                 Model model) {
        User userRequested = userService.findByUsername(username);
        Repository repositoryRequested = repositoryRepository.findByUrl(repositoryUrl);
        if (userRequested == null || repositoryRequested == null) {
            return "error";
        }
        model.addAttribute("repository", repositoryRequested);
        return "repository";
    }
}
