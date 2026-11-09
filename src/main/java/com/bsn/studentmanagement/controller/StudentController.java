package com.bsn.studentmanagement.controller;

import com.bsn.studentmanagement.dto.CourseDTO;
import com.bsn.studentmanagement.dto.StudentDTO;
import com.bsn.studentmanagement.service.impl.StudentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.model.IModel;

@Controller
@RequestMapping("/students")
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/new")
    public String showCreateStudent(Model model) {
        log.info("Get /new - showing create student page");
        model.addAttribute("studentDto", new StudentDTO());
        return "add-student";
    }

    @GetMapping("/list")
    public String listStudent() {
        log.info("Get /new - showing list student page");
        return "students";
    }

    @PostMapping("/save")
    public String createStudent(@Valid @ModelAttribute("studentDto") StudentDTO studentDTO,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        log.info("Post /save - create student request recieved.");

        if(bindingResult.hasErrors()){
            return "add-student";
        }

        if(studentService.existsByEmailIgnoreCase(studentDTO.getEmail())){
            log.error("Post /save - email must be unique.");

            bindingResult.rejectValue("email" , null , "email must be unique");

            return "add-student";
        }

        studentService.createStudent(studentDTO);
        redirectAttributes.addFlashAttribute("message", "Student is added successfully!");
        return "redirect:/students/list";
    }


}
