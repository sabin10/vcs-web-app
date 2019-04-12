package com.sabinhantu.vcs.repository;

import com.sabinhantu.vcs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findByRepositories_Url(String url);
}
