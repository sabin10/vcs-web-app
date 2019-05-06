package com.sabinhantu.vcs;

import com.sabinhantu.vcs.model.Project;
import com.sabinhantu.vcs.model.User;
import com.sabinhantu.vcs.repository.BranchRepository;
import com.sabinhantu.vcs.repository.CommitRepository;
import com.sabinhantu.vcs.repository.ProjectRepository;
import com.sabinhantu.vcs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VcsApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private BranchRepository branchRepository;


    public static void main(String[] args) {
        SpringApplication.run(VcsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User sabin = new User("sabin", "sabin@gmail.com","sabin");
        User vasile = new User("vasile", "vasile@gmail.com","vasile");
        Project rep1 = new Project("sabin repo");

        projectRepository.save(rep1);
        sabin.addProject(rep1);
        userService.save(sabin);

        userService.save(vasile);
        userService.save(new User("cosmin", "cosmin@gmail.com","cosmin"));
        userService.save(new User("voinea", "voinea@gmail.com","voinea"));
        userService.save(new User("elon", "elon@gmail.com","elon"));
    }
}
