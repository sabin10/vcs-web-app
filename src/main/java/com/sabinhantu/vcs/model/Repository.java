package com.sabinhantu.vcs.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Repository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToMany(mappedBy = "repositories")
    private Set<User> users;

    public Repository() {
        users = new HashSet<>();
    }

    public Repository(String title) {
        this();
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
