package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @Autowired
    private UserService userService;

    @GetMapping({"", "/", "index"})
    public String index(Model model) {
        model.addAttribute("users", userService.findAll());
        String usernameLoggedIn = AccountController.loggedInUsername();
        return "redirect:/" + usernameLoggedIn;
    }
}
