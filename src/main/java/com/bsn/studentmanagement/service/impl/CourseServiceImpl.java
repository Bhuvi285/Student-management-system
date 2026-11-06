package com.bsn.studentmanagement.service.impl;

import com.bsn.studentmanagement.dto.CourseDTO;
import com.bsn.studentmanagement.model.Courses;
import com.bsn.studentmanagement.repository.CourseRepository;
import org.modelmapper.ModelMapper;

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
}
