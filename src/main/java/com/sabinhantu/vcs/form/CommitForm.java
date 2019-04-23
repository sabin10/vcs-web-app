package com.sabinhantu.vcs.form;

import javax.validation.constraints.NotEmpty;

public class CommitForm {
    @NotEmpty
    private String name;

    private String description;

    public CommitForm() {
    }

    public CommitForm(String name) {
        this.name = name;
    }

    public CommitForm(String name, String description) {
        this.name = name;
        this.description = description;
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
}
