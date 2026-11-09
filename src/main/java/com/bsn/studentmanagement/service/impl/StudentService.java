package com.bsn.studentmanagement.service.impl;

import com.bsn.studentmanagement.dto.StudentDTO;

public interface StudentService {

    boolean existsByEmailIgnoreCase(String email);

    StudentDTO createStudent(StudentDTO studentDTO);
}
