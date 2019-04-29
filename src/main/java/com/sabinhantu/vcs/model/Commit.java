package com.sabinhantu.vcs.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
public class Commit implements Comparable<Commit> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Date createdAt;

    @NotNull
    private String name;

    private String description;

    @ManyToMany(mappedBy = "commits")
    private Set<Branch> branches;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    @OneToMany(mappedBy = "commit")
    @OrderBy("id")
    private SortedSet<DeltaSimulate> deltaSimulateSet;

    public Commit() {
        branches = new HashSet<>();
        deltaSimulateSet = new TreeSet<>();
    }

    public Commit(@NotNull String name) {
        this();
        this.name = name;
    }

    public Commit(@NotNull String name, String description) {
        this(name);
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Branch> getBranches() {
        return branches;
    }

    public void setBranches(Set<Branch> branches) {
        this.branches = branches;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public SortedSet<DeltaSimulate> getDeltaSimulateSet() {
        return deltaSimulateSet;
    }

    public void setDeltaSimulateSet(SortedSet<DeltaSimulate> deltaSimulateSet) {
        this.deltaSimulateSet = deltaSimulateSet;
    }

    public void addDeltaSimulate(DeltaSimulate deltaSimulate) {
        this.deltaSimulateSet.add(deltaSimulate);
        deltaSimulate.setCommit(this);
    }

    //sort descending
    @Override
    public int compareTo(Commit o) {
        if (this.id < o.getId()) {
            return 1;
        } else if (this.id > o.getId()) {
            return -1;
        }
        return 0;
    }
}
