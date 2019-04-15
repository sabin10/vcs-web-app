package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.repository.ProjectRepository;
import com.sabinhantu.vcs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BranchController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/{username}/{projectUrl}/master")
    public String toMasterBranch(@PathVariable final String username,
                                 @PathVariable final String projectUrl) {
        return "redirect:/" + username + "/" + projectUrl;
    }

}
