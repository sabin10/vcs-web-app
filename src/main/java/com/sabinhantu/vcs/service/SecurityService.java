package com.sabinhantu.vcs.service;

/**
Provide current logged user
and enables autoLogin after Registration Process

 **/

public interface SecurityService {
    String findLoggedInUsername();

    void autoLogin(String username, String password);
}
