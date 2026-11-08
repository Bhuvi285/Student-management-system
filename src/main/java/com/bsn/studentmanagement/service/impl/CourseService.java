package com.bsn.studentmanagement.service.impl;

import com.bsn.studentmanagement.dto.CourseDTO;
import org.springframework.data.domain.Page;

public interface CourseService {
    CourseDTO createCourse(CourseDTO courseDTO);

    boolean existsByCourseCode(String code);

    boolean existsByCourseCodeAndIdNot(String code , Long id);


    Page<CourseDTO> getCourses(int page , int size);

    CourseDTO getCourseById(Long id);

    CourseDTO updateCourse(Long id , CourseDTO courseDTO);
}
