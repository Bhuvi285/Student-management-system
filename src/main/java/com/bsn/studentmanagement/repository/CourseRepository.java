package com.bsn.studentmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bsn.studentmanagement.model.Courses;

public interface CourseRepository extends JpaRepository<Courses, Long> {
	
}
