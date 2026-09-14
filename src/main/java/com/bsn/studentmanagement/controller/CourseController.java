package com.bsn.studentmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bsn.studentmanagement.dto.CourseDTO;

@Controller
@RequestMapping("/course")
public class CourseController {
	
	@GetMapping("/new")
	public String showCreateCourse(Model model) {
		model.addAttribute("courseDto" , new CourseDTO());
		return "add-course";
	}
}
