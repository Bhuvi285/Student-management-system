package com.bsn.studentmanagement.service.impl;

import com.bsn.studentmanagement.dto.CourseDTO;
import com.bsn.studentmanagement.model.Courses;
import com.bsn.studentmanagement.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);


    private final CourseRepository courseRepository;
    private final ModelMapper mapper;
    CourseServiceImpl(CourseRepository courseRepository , ModelMapper mapper){
            this.courseRepository = courseRepository;
            this.mapper = mapper;
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        log.info("creating course with code: {}",courseDTO.getCourseCode());
        Courses courses = mapper.map(courseDTO , Courses.class);
        courseRepository.save(courses);
        return mapper.map(courses , CourseDTO.class);
    }

    @Override
    public boolean existsByCourseCode(String code) {
        log.info("Checking if code exists: {}" , code);
        return courseRepository.existsByCourseCodeIgnoreCase(code);
    }
}
