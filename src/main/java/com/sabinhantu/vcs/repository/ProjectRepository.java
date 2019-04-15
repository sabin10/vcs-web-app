package com.sabinhantu.vcs.repository;

import com.sabinhantu.vcs.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByTitle(String title);
    Project findByUrl(String url);
    List<Project> findByUsers_Username(String username);

    @Modifying
    @Transactional
    void deleteById(Long id);
}
