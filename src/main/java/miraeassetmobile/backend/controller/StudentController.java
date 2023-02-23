package miraeassetmobile.backend.controller;

import miraeassetmobile.backend.domain.dto.students.StudentAccountResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentJobResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentTransferSelectorResponseDto;
import miraeassetmobile.backend.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @GetMapping("/{student_id}/job")
    public ResponseEntity<StudentJobResponseDto> getStudentJobInfo(@PathVariable(value = "student_id") Long studentId){
        return ResponseEntity.ok(studentService.getStudentJobInfo(studentId));
    }




}
