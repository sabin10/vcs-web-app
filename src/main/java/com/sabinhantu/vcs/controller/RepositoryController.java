package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.model.Repository;
import com.sabinhantu.vcs.model.User;
import com.sabinhantu.vcs.repository.RepositoryRepository;
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
public class RepositoryController {
    @Autowired
    private UserService userService;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{username}/{repositoryUrl}")
    public String userRepository(@PathVariable final String username,
                                 @PathVariable final String repositoryUrl,
                                 Model model) {
        if (!doesRepositoryExist(username, repositoryUrl)) {
            return "error";
        }
        User userRequested = userService.findByUsername(username);
        Repository repositoryRequested = repositoryRepository.findByUrl(repositoryUrl);
        List<User> usersOwners = userRepository.findByRepositories_Url(repositoryUrl);
        model.addAttribute("user", userRequested);
        model.addAttribute("repository", repositoryRequested);
        model.addAttribute("usersOwners", usersOwners);
        return "repository";
    }

    @GetMapping("/{username}/{repositoryUrl}/settings")
    public String repositorySettings(@PathVariable final String username,
                                     @PathVariable final String repositoryUrl,
                                     Model model) {
        if (!doesRepositoryExist(username, repositoryUrl)) {
            return "error";
        }
        User userOwner = userService.findByUsername(username);
        Repository repositoryRequested = repositoryRepository.findByUrl(repositoryUrl);
        model.addAttribute("userOwner", userOwner);
        model.addAttribute("repository", repositoryRequested);

        return "repositorysettings";
    }

    //post add member ownership to repository
    @PostMapping("/{usernameUrl}/{repositoryUrl}/addmember")
    public String addMemberOwnershipToRepository(@PathVariable final String usernameUrl,
                                                 @PathVariable final String repositoryUrl,
                                                 @ModelAttribute("userForm") User userForm,
                                                 Model model) {
        User newMemberUser = userService.findByUsername(userForm.getUsername());
        Repository repository = repositoryRepository.findByUrl(repositoryUrl);

        if (newMemberUser == null || userOwnsRepository(newMemberUser, repository)) {
            return "redirect:/" + usernameUrl + "/" + repositoryUrl + "/settings?error";
        }

        newMemberUser.addRepository(repository);
        userRepository.save(newMemberUser);

        return "redirect:/" + usernameUrl + "/" + repositoryUrl;
    }


    // TODO: Functie asa sau throw exception? intreaba Karla
    private boolean doesRepositoryExist(String username, String repositoryUrl) {
        User userRequested = userService.findByUsername(username);
        Repository repositoryRequested = repositoryRepository.findByUrl(repositoryUrl);
        if (userRequested == null || repositoryRequested == null) {
            return false;
        }
        return true;
    }

    private boolean userOwnsRepository(User user, Repository repository) {
        if (user.getRepositories().contains(repository))
            return true;
        return false;
    }
}
