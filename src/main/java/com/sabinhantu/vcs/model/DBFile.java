package com.sabinhantu.vcs.model;

import javax.persistence.*;

@Entity
public class DBFile implements Comparable<DBFile>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String fileType;

    @Lob
    @Column(length = 10000, name = "data")
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "commit_id")
    private Commit lastCommit;

    public DBFile() {
    }

    public DBFile(String fileName, String fileType, byte[] data) {
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

    public Commit getLastCommit() {
        return lastCommit;
    }

    public void setLastCommit(Commit commit) {
        this.lastCommit = commit;
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
