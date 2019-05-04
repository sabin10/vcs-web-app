package com.sabinhantu.vcs.model;

import javax.persistence.*;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
public class DBFile implements Comparable<DBFile>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String fileType;

    @Lob
    //@Column(name = "data", length = 5000)
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToMany
    @JoinTable(name = "dbfile_commit",
            joinColumns = {@JoinColumn(name = "dbfile_id")},
            inverseJoinColumns = {@JoinColumn(name = "commit_id")})
    @OrderBy("id")
    private SortedSet<Commit> commits;

    public DBFile() {
        commits = new TreeSet<>();
    }

    public DBFile(String fileName, String fileType, byte[] data) {
        this();
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public SortedSet<Commit> getCommits() {
        return commits;
    }

    public void setCommits(SortedSet<Commit> commits) {
        this.commits = commits;
    }

    public Commit getLastCommit() {
        return this.commits.first();
    }

    public String getStringData() {
        String original = new String(this.data);
        return original;
    }

    //sort ascending
    @Override
    public int compareTo(DBFile o) {
        if (this.id < o.getId()) {
            return -1;
        } else if (this.id > o.getId()) {
            return 1;
        }
        return 0;
    }
}
