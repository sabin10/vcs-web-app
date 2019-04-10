package com.sabinhantu.vcs.form;

import com.sabinhantu.vcs.constraint.FieldMatch;

import javax.validation.constraints.NotNull;

@FieldMatch(first = "password", second = "passwordConfirm", message = "The password fields must match")
public class UserForm {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String passwordConfirm;

    public UserForm() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
