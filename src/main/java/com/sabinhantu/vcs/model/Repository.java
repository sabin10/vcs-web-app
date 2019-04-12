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

    private String url;

    @ManyToMany(mappedBy = "repositories")
    private Set<User> users;

    public Repository() {
        users = new HashSet<>();
    }

    public Repository(String title) {
        this();
        this.title = title;
        this.url = titleToUrl(title).toString();
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    private StringBuilder titleToUrl(String title) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < title.length(); i++) {
            if (Character.isLetter(title.charAt(i))) {
                result.append(Character.toLowerCase(title.charAt(i)));
            } else if (title.charAt(i) == ' '){
                result.append('-');
            }
        }
        int i = result.length() - 1;
        while (result.charAt(i) == '-'){
            result.deleteCharAt(i);
            i--;
        }
        return result;
    }
}
