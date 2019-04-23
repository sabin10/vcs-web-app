package com.sabinhantu.vcs.form;

public class FileForm {
    String fileName;
    String fileData;

    public FileForm() {
    }

    public FileForm(String fileName, String fileData) {
        this.fileName = fileName;
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }
}
