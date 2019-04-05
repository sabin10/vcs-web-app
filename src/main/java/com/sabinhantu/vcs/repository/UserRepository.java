package com.sabinhantu.vcs.repository;

import com.sabinhantu.vcs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
