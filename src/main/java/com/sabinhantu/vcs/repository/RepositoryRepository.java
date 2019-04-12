package com.sabinhantu.vcs.repository;

import com.sabinhantu.vcs.model.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositoryRepository extends JpaRepository<Repository, Long> {
    Repository findByTitle(String title);
    Repository findByUrl(String url);
    List<Repository> findByUsers_Username(String username);
}
