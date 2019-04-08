package com.sabinhantu.vcs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/user")
    public String getAccount() {
        return "user";
    }
}
