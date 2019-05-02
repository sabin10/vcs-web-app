package com.sabinhantu.vcs.controller;

import com.sabinhantu.vcs.form.FileForm;
import com.sabinhantu.vcs.model.Branch;
import com.sabinhantu.vcs.model.Commit;
import com.sabinhantu.vcs.model.DBFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@Controller
public class FileDisplayController {
    @Autowired
    private BranchController branchController;

    @GetMapping("/{username}/{projectUrl}/{branchName}/file/{fileName}")
    public String displayFile(@PathVariable final String username,
                              @PathVariable final String projectUrl,
                              @PathVariable final String branchName,
                              @PathVariable final String fileName,
                              Model model) {
        Branch currentBranch = branchController.getCurrentBranch(username, projectUrl, branchName);
        Set<DBFile> files = currentBranch.getFiles();
        for (DBFile file : files) {
            if (file.getFileName().equals(fileName)) {
                FileForm fileForm = new FileForm(file.getFileName(), file.getStringData());

                Set<Commit> commits = file.getCommits();

//                // reverse set
//                SortedSet<Commit> commits = new TreeSet<>(Collections.reverseOrder());
//                commits.addAll(file.getCommits());
                model.addAttribute("fileForm", fileForm);
                model.addAttribute("commits", commits);
            }
        }
        return "filedisplay";
    }

}
