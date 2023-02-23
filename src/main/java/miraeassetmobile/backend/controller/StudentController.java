package miraeassetmobile.backend.controller;

import miraeassetmobile.backend.domain.dto.students.StudentAccountResponseDto;
import miraeassetmobile.backend.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/student")
@RestController
public class StudentController {

    private StudentService studentService;

    public StudentController(StudentService studentService){
        this.studentService =studentService;
    }




    @GetMapping("/{student_id}/account")
    public ResponseEntity<StudentAccountResponseDto> getStudentAccountInfo(@PathVariable(value = "student_id") Long id){

        return ResponseEntity.ok(studentService.getStudentAccountInfo(id));

    }


    @GetMapping("/{student_id}/transaction")
    public (@PathVariable(value = "student_id") Long id){

        return ResponseEntity.ok(studentService.getStudentAccountInfo(id));

    }


}
