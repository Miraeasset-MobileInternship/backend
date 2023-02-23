package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(description = "학생의 계좌 정보를 조회(보유 금액등) , 학생 - 홈 화면 본인 계좌 카드 부분")
    public ResponseEntity<StudentAccountResponseDto> getStudentAccountInfo(@PathVariable(value = "student_id") Long id){

        return ResponseEntity.ok(studentService.getStudentAccountInfo(id));

    }


    @GetMapping("/{student_id}/job")
    @Operation(description = "학생의 직업 카드에 필요한 정보 조회, 학생 - 직업 화면 학생 본인 직업카드")
    public ResponseEntity<StudentJobResponseDto> getStudentJobInfo(@PathVariable(value = "student_id") Long studentId){
        return ResponseEntity.ok(studentService.getStudentJobInfo(studentId));
    }




}
