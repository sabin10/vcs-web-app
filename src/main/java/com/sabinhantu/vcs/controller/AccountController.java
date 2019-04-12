package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.model.User;
import com.sabinhantu.vcs.repository.RepositoryRepository;
import com.sabinhantu.vcs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AccountController {
    @Autowired
    private UserService userService;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @GetMapping("/user")
    public String getAccount(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String usernameLoggedIn = null;
        if (principal instanceof UserDetails) {
            usernameLoggedIn = ((UserDetails)principal).getUsername();
        } else {
            usernameLoggedIn = principal.toString();
        }

        model.addAttribute("currentuser", usernameLoggedIn);
        // TODO: ar trebui cu try and catch? Intreaba Karla

        return "redirect:/" + usernameLoggedIn;
    }

    @GetMapping("/{username}")
    public String userAccount(@PathVariable final String username, Model model) {
        User userRequested = userService.findByUsername(username);
        if (userRequested == null) {
            return "error";
        }
        model.addAttribute("userRequested", username);
        model.addAttribute("repositories", repositoryRepository.findByUsers_Username(username));
        return "user";
    }
}
