package com.sabinhantu.vcs.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    private String url;

    private String description;

    @CreationTimestamp
    private Date createdAt;

    @ManyToMany(mappedBy = "projects")
    private Set<User> users;

    /**
     * Hibernate will not create association table for unidirectional OneToMany
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    private Set<Branch> branches;

    public Project() {
        users = new HashSet<>();
        branches = new HashSet<>();
        branches.add(new Branch("master"));
    }

    public Project(String title) {
        this();
        this.title = title;
        this.url = titleToUrl(title).toString();
    }

    public Project(String title, String description) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Branch> getBranches() {
        return branches;
    }

    public void setBranches(Set<Branch> branches) {
        this.branches = branches;
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
