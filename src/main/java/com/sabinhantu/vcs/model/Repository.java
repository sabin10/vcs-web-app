package com.sabinhantu.vcs.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Repository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    private String url;

    private String description;

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

    public Repository(String title, String description) {
        this(title);
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
