package com.sabinhantu.vcs.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class DeltaSimulate implements Comparable<DeltaSimulate>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "dbfile_id")
    private DBFile file;

    @ManyToOne
    @JoinColumn(name = "commit_id")
    private Commit commit;

    private String deltaType;

    private int positionOriginal;
    private String linesOriginal;

    private int positionRevised;
    private String linesRevised;

    public DeltaSimulate() {
    }

    public DeltaSimulate(String deltaType, int positionOriginal, String linesOriginal, int positionRevised, String linesRevised) {
        this.deltaType = deltaType;
        this.positionOriginal = positionOriginal;
        this.linesOriginal = linesOriginal;
        this.positionRevised = positionRevised;
        this.linesRevised = linesRevised;
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

    public DBFile getFile() {
        return file;
    }

    public void setFile(DBFile file) {
        this.file = file;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public String getDeltaType() {
        return deltaType;
    }

    public void setDeltaType(String deltaType) {
        this.deltaType = deltaType;
    }

    public int getPositionOriginal() {
        return positionOriginal;
    }

    public void setPositionOriginal(int positionOriginal) {
        this.positionOriginal = positionOriginal;
    }

    public String getLinesOriginal() {
        return linesOriginal;
    }

    public void setLinesOriginal(String linesOriginal) {
        this.linesOriginal = linesOriginal;
    }

    public int getPositionRevised() {
        return positionRevised;
    }

    public void setPositionRevised(int positionRevised) {
        this.positionRevised = positionRevised;
    }

    public String getLinesRevised() {
        return linesRevised;
    }

    public void setLinesRevised(String linesRevised) {
        this.linesRevised = linesRevised;
    }

    @Override
    public int compareTo(DeltaSimulate o) {
        if (this.id > o.getId()) {
            return 1;
        } else if (this.id < o.getId()) {
            return -1;
        }
        return 0;
    }
}
