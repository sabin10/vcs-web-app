package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.model.DBFile;
import com.sabinhantu.vcs.repository.DBFileRepository;
import com.sabinhantu.vcs.service.DBFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FileController {

    //private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private DBFileStorageService dbFileStorageService;

    @Autowired
    private DBFileRepository dbFileRepository;

    @GetMapping("/addfiles")
    public String addFilesView(Model model) {
        model.addAttribute("files", dbFileRepository.findAll());
        List<String> dataStrings = new ArrayList<>();
        if (dbFileRepository.findAll().isEmpty() == false) {
            List<DBFile> files = dbFileRepository.findAll();
            for (DBFile file : files) {
                dataStrings.add(new String(file.getData()));
            }
            model.addAttribute("dataStrings", dataStrings);
        }
        return "addfiles";
    }

    @PostMapping("/addfile")
    public String uploadSingleFile(@RequestParam("file") MultipartFile file) {
        dbFileStorageService.storeFile(file);
        return "redirect:/addfiles";
    }

    @PostMapping("/addfilemultiple")
    public String uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        for (MultipartFile file : files) {
            dbFileStorageService.storeFile(file);
        }
        return "redirect:/addfiles";
    }


}
