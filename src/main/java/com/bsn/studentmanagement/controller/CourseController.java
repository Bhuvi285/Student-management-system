package com.bsn.studentmanagement.controller;

import com.bsn.studentmanagement.dto.CourseDTO;
import com.bsn.studentmanagement.service.impl.CourseService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/course")
public class CourseController {

    private static final Logger log = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;

    CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/new")
    public String showCreateCourse(Model model) {
        log.info("Get /course/new - showing create course page");
        model.addAttribute("courseDto", new CourseDTO());
        return "add-course";
    }

    @GetMapping("/list")
    public String listCourse(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size, Model model, @RequestParam(value = "message", required = false) String message) {
        log.info("Get /course/list - showing course list page.");

        Page<CourseDTO> courses = courseService.getCourses(page, size);
        model.addAttribute("courses", courses);
        model.addAttribute("message", message);
        return "courses";
    }

    @PostMapping
    public String CreateCourse(@Valid @ModelAttribute("courseDto") CourseDTO courseDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        log.info("Post /course - create course request received.");

        if (bindingResult.hasErrors()) {
            log.error("Post /course - page return due to validation error.");
            return "add-course";
        }

        if (courseService.existsByCourseCode(courseDTO.getCourseCode())) {
            log.error("Post /course - Course must be unique.");
            bindingResult.rejectValue("courseCode", null, "Course must be unique.");
            return "add-course";
        }

        courseService.createCourse(courseDTO);

        // FIXED: Changed addAttribute to addFlashAttribute to prevent URL pollution
        redirectAttributes.addFlashAttribute("message", "Course is created successfully!");
        log.info("Post /course - course successfully created.");

        return "redirect:/course/list";
    }

    @GetMapping("/{id}")
    public String getCourseById(@PathVariable Long id, Model model) {
        CourseDTO course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        return "view-course";
    }

    @GetMapping("/{id}/edit")
    public String editCourse(@PathVariable Long id, Model model) {
        CourseDTO course = courseService.getCourseById(id);
        model.addAttribute("courseDto", course);
        return "edit-course";
    }

    @PostMapping("/{id}/update")
    public String updateCourse(@PathVariable Long id, @Valid @ModelAttribute("courseDto") CourseDTO courseDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        log.info("Post /{id}/course - update course request received. {}", id);

        if (bindingResult.hasErrors()) {
            log.error("Post /{id}/course - page return due to validation error.");
            return "add-course";
        }

        if (courseService.existsByCourseCodeAndIdNot(courseDTO.getCourseCode() , id)) {
            log.error("Post /{id}/course - Course must be unique.");
            bindingResult.rejectValue("courseCode", null, "Course must be unique.");
            return "edit-course";
        }

        courseService.updateCourse(id, courseDTO);

        // FIXED: Changed addAttribute to addFlashAttribute to prevent URL pollution
        redirectAttributes.addAttribute("message", "Course is updated successfully!");
        log.info("Post /{id}/course - course successfully updated.");

        return "redirect:/course/list";
    }
}
