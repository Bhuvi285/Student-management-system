package com.bsn.studentmanagement.repository;

import com.bsn.studentmanagement.model.Students;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Students, Long> {
}
