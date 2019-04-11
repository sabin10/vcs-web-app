package com.sabinhantu.vcs;

import com.sabinhantu.vcs.model.User;
import com.sabinhantu.vcs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VcsApplication implements CommandLineRunner {

    @Autowired
    private UserService userService;


    public static void main(String[] args) {
        SpringApplication.run(VcsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        userService.save(new User("sabin", "sabin@gmail.com","sabin"));
        userService.save(new User("vasile", "vasile@gmail.com","vasile"));
        userService.save(new User("cosmin", "cosmin@gmail.com","cosmin"));
        userService.save(new User("voinea", "voinea@gmail.com","voinea"));
        userService.save(new User("elon", "elon@gmail.com","elon"));
    }
}
