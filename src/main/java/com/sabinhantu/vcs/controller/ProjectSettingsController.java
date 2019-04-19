package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.form.ProjectForm;
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

import java.util.Set;

@Controller
public class ProjectSettingsController {
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectController projectController;

    @PostMapping("/{usernameUrl}/{projectUrl}/edit")
    public String editProjectCredentials(@PathVariable final String usernameUrl,
                                         @PathVariable final String projectUrl,
                                         @ModelAttribute("projectForm") ProjectForm projectForm) {
        User user = userService.findByUsername(usernameUrl);
        Set<Project> projects = user.getProjects();
        for (Project project : projects) {
            if (project.getUrl().equals(projectUrl)) {
                if (!checkIfFormInputEmpty(projectForm.getTitle())) {
                    project.setTitle(projectForm.getTitle());
                }
                if (!checkIfFormInputEmpty(projectForm.getDescription())) {
                    project.setDescription(projectForm.getDescription());
                }
                projectRepository.save(project);
                return "redirect:/{usernameUrl}/" + project.getUrl();
            }
        }
        return "redirect:/" + usernameUrl + "/" + projectUrl + "/settings";
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

        if (newMemberUser == null || projectController.userOwnsRepository(newMemberUser, project)) {
            return "redirect:/" + usernameUrl + "/" + projectUrl + "/settings?error";
        }
        newMemberUser.addProject(project);
        userRepository.save(newMemberUser);

        return "redirect:/" + usernameUrl + "/" + projectUrl + "/settings";
    }

    @GetMapping("/{usernameUrl}/{projectId}/delete")
    public String deleteProject(@PathVariable final String usernameUrl,
                                @PathVariable final String projectId) {
        User userRequested = userService.findByUsername(usernameUrl);
        Project project = projectRepository.getOne(Long.parseLong(projectId));
        Set<User> usersOwners = project.getUsers();

        /**ManyToMany DELETE**/
        for (User owner : usersOwners) {
            owner.getProjects().remove(project);
        }
        project.getUsers().clear();
        projectRepository.deleteById(Long.parseLong(projectId));
        return "redirect:/" + usernameUrl;
    }

    protected boolean checkIfFormInputEmpty(String formParameter) {
        if (formParameter.trim().compareTo("") == 0) {
            return true;
        }
        return false;
    }

}
