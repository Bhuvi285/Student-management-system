package com.bsn.studentmanagement.service.impl;

import com.bsn.studentmanagement.dto.CourseDTO;

public interface CourseService {
    CourseDTO createCourse(CourseDTO courseDTO);

    boolean existsByCourseCode(String code);
}
