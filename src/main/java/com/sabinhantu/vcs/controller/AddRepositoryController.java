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
    public String showAddRepository(@PathVariable final String username,
                                    Model model) {
        String usernameLoggedIn = AccountController.loggedInUsername();
        model.addAttribute("username", usernameLoggedIn);
        return "addrepository";
    }

    @PostMapping("/{usernameUrl}/addrepository")
    public String addRepository(@PathVariable final String usernameUrl,
                                @ModelAttribute("repositoryForm") Repository repositoryForm,
                                Model model) {
        Repository newRepository = new Repository(repositoryForm.getTitle(), repositoryForm.getDescription());
        User currentUser = userService.findByUsername(usernameUrl);

        repositoryRepository.save(newRepository);
        currentUser.addRepository(newRepository);
        userRepository.save(currentUser);

        return "redirect:/" + usernameUrl + "/" + newRepository.getUrl();
    }
}
