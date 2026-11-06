package com.bsn.studentmanagement.controller;

import com.bsn.studentmanagement.service.impl.CourseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bsn.studentmanagement.dto.CourseDTO;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/course")
public class CourseController {

	private final CourseService courseService;

	CourseController(CourseService courseService){
		this.courseService = courseService;
	}
	
	@GetMapping("/new")
	public String showCreateCourse(Model model) {
		model.addAttribute("courseDto" , new CourseDTO());
		return "add-course";
	}
	
	@GetMapping("/list")
	public String listCourse() {
		return "courses";
	}

	@PostMapping
	public String CreateCourse(@Valid @ModelAttribute("courseDTO") CourseDTO courseDTO ,
							   BindingResult bindingResult,
							   Model model,
							   RedirectAttributes redirectAttributes){

		if(bindingResult.hasErrors()) {
			return "add-course";
		}
		if(courseService.existsByCode(courseDTO.getCourseCode())) {
			bindingResult.rejectValue("courseCode" , "Course Must be unique");
			return "add-course";

		}

		courseService.createCourse(courseDTO);
		redirectAttributes.addAttribute("message" , "Course is created successfully ");
		return "/course/list";
	}
}
