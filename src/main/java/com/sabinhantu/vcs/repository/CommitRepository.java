package com.sabinhantu.vcs.repository;

import com.sabinhantu.vcs.model.Branch;
import com.sabinhantu.vcs.model.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommitRepository extends JpaRepository<Commit, Long> {
    List<Branch> findByBranches_Name(String name);

    @Modifying
    @Transactional
    void deleteById(Long id);
}
