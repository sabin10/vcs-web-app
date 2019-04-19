package com.sabinhantu.vcs.repository;

import com.sabinhantu.vcs.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {

}
