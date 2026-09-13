package com.bsn.studentmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CourseDTO {

	private Long id;
	
	@NotBlank(message = "Course name is required.")
	@Size(max = 150 , message = "Max of 150 characters allowed.")
	private String courseName;
	
	@NotBlank(message = "Course Code is required.")
	private String courseCode;
	
	@NotBlank(message = "Course duration is required.")
	private String Duration;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getDuration() {
		return Duration;
	}

	public void setDuration(String duration) {
		Duration = duration;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@NotBlank(message = "Course Fee is required.")
	private BigDecimal fee;
	
	@Size(max = 500,message = "Max of 500 characters allowed.")
	private String description;
	
}
