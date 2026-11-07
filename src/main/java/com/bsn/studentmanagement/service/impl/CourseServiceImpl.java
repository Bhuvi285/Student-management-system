package com.bsn.studentmanagement.service.impl;

import com.bsn.studentmanagement.dto.CourseDTO;
import com.bsn.studentmanagement.model.Courses;
import com.bsn.studentmanagement.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ModelMapper mapper;
    CourseServiceImpl(CourseRepository courseRepository , ModelMapper mapper){
            this.courseRepository = courseRepository;
            this.mapper = mapper;
    }

    @Override
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Courses courses = mapper.map(courseDTO , Courses.class);
        courseRepository.save(courses);
        return mapper.map(courses , CourseDTO.class);
    }

    @Override
    public boolean existsByCourseCode(String code) {
        return courseRepository.existsByCourseCodeIgnoreCase(code);
    }
}
