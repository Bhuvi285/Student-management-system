package com.bsn.studentmanagement.controller;

import com.bsn.studentmanagement.exception.GlobalExceptionHandler;
import com.bsn.studentmanagement.service.impl.CourseService;
import jakarta.validation.Valid;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger log = LoggerFactory.getLogger(CourseController.class);

	private final CourseService courseService;

	CourseController(CourseService courseService){
		this.courseService = courseService;
	}
	
	@GetMapping("/new")
	public String showCreateCourse(Model model) {
		log.info("Get /course/new - showing create course page");
		model.addAttribute("courseDto" , new CourseDTO());
		return "add-course";
	}
	
	@GetMapping("/list")
	public String listCourse() {
		return "courses";
	}

	@PostMapping
	public String CreateCourse(@Valid @ModelAttribute("courseDto") CourseDTO courseDTO ,
							   BindingResult bindingResult,
							   Model model,
							   RedirectAttributes redirectAttributes){

		log.info("Post /course - create  course request recieved.");

		if(bindingResult.hasErrors()) {
			log.error("Post /course - page return due to validation error.");
			return "add-course";
		}
		if(courseService.existsByCourseCode(courseDTO.getCourseCode())) {
			log.error("Post /course - Course must be unique.");

			bindingResult.rejectValue(
					"courseCode",
					null,
					"Course must be unique."
			);
			return "add-course";

		}

		courseService.createCourse(courseDTO);
		redirectAttributes.addAttribute("message" , "Course is created successfully ");
		log.info("Post /course - create  course successfully created.");

		return  "redirect:/course/list";
	}
}
