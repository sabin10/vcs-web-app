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
        StringBuilder stringBuilder = new StringBuilder();
        // remove tab space from first line
        stringBuilder.append("\n");
        stringBuilder.append(fileData);
        return stringBuilder.toString();
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }
}
