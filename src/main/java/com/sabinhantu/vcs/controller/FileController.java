package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.repository.DBFileRepository;
import com.sabinhantu.vcs.service.DBFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
        if (dbFileRepository.findAll().isEmpty() == false) {
            String dataString = new String(dbFileRepository.getOne(1L).getData());
            model.addAttribute("dataString", dataString);
        }
        return "addfiles";
    }

    @PostMapping("/addfile")
    public String uploadSingleFile(@RequestParam("file") MultipartFile file) {
        dbFileStorageService.storeFile(file);
        return "redirect:/addfiles";
    }
}
