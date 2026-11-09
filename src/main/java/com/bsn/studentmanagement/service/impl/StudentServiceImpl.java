package com.bsn.studentmanagement.service.impl;

import com.bsn.studentmanagement.dto.StudentDTO;
import com.bsn.studentmanagement.model.Students;
import com.bsn.studentmanagement.repository.StudentRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final ModelMapper mapper;

    public StudentServiceImpl(StudentRepository studentRepository, ModelMapper mapper) {
        this.studentRepository = studentRepository;
        this.mapper = mapper;
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        log.info("email from create student");
        return studentRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public StudentDTO createStudent(StudentDTO studentDTO) {
        log.info("saving student data");

        Students students = mapper.map(studentDTO , Students.class);
        Students saved = studentRepository.save(students);

        return mapper.map(saved , StudentDTO.class);
    }
}
