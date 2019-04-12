package com.sabinhantu.vcs;

import com.sabinhantu.vcs.model.Repository;
import com.sabinhantu.vcs.model.User;
import com.sabinhantu.vcs.repository.RepositoryRepository;
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
    private RepositoryRepository repositoryRepository;


    public static void main(String[] args) {
        SpringApplication.run(VcsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User sabin = new User("sabin", "sabin@gmail.com","sabin");
        User vasile = new User("vasile", "vasile@gmail.com","vasile");
        Repository rep1 = new Repository("sabinrepo");
        Repository rep2 = new Repository("vasilerepo");

        repositoryRepository.save(rep1);
        repositoryRepository.save(rep2);

        sabin.addRepository(rep1);
        sabin.addRepository(rep2);
        vasile.addRepository(rep2);

        userService.save(sabin);
        userService.save(vasile);
        userService.save(new User("cosmin", "cosmin@gmail.com","cosmin"));
        userService.save(new User("voinea", "voinea@gmail.com","voinea"));
        userService.save(new User("elon", "elon@gmail.com","elon"));
    }
}
