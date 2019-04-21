package com.sabinhantu.vcs.service;

import com.sabinhantu.vcs.exception.FileStorageException;
import com.sabinhantu.vcs.exception.MyFileNotFoundException;
import com.sabinhantu.vcs.model.DBFile;
import com.sabinhantu.vcs.repository.DBFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DBFileStorageService {

    @Autowired
    private DBFileRepository dbFileRepository;

    public DBFile storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            DBFile dbFile = new DBFile(fileName, file.getContentType(), file.getBytes());

            return dbFileRepository.save(dbFile);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public DBFile getFile(Long fileId) {
        DBFile dbFile = dbFileRepository.getOne(fileId);
        if (dbFile == null) {
            throw new MyFileNotFoundException("File not found with id " + fileId);
        }
        return dbFile;
    }
}
