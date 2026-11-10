package com.bsn.studentmanagement.repository;

import com.bsn.studentmanagement.model.Students;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Students, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Page<Students> findByActiveTrue(Pageable pageable);

}
