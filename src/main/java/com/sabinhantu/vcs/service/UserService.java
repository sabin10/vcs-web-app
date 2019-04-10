package com.sabinhantu.vcs.service;

import com.sabinhantu.vcs.model.User;

import java.util.List;

public interface UserService {
    //register new user
    void save(User user);

    User findByUsername(String username);

    List<User> findAll();
}
