package miraeassetmobile.backend.controller;

import miraeassetmobile.backend.service.StudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/student")
@RestController
public class StudentController {

    private StudentService studentService;

    public StudentController(StudentService studentService){
        this.studentService =studentService;
    }



}
