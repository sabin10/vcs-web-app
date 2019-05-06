package com.sabinhantu.vcs.form;

public class FileForm {
    String fileName;
    String fileData;
    Long fileId;

    public FileForm() {
    }

    public FileForm(String fileName, String fileData, Long fileId) {
        this.fileName = fileName;
        this.fileData = fileData;
        this.fileId = fileId;
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

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
}


