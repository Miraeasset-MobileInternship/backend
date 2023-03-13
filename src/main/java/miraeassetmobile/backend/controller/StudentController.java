package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import miraeassetmobile.backend.domain.dto.students.StudentAccountResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentJobResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentSalaryResponseDto;
import miraeassetmobile.backend.domain.dto.transactions.MoneyChangeResponseDto;
import miraeassetmobile.backend.service.StudentService;
import miraeassetmobile.backend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/student")
@RestController
public class StudentController {

    private StudentService studentService;
    private TransactionService transactionService;

    public StudentController(StudentService studentService, TransactionService transactionService){
        this.studentService =studentService;
        this.transactionService=transactionService;
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


    //이전날 대비 변동가격
    @GetMapping("/{student_id}/change")
    @Operation(description = "학생의 계좌금액 : 전날대비 변동가격 - 직업 화면 학생 본인 계좌")
    public ResponseEntity<MoneyChangeResponseDto> getStudentChangeMoney(@PathVariable(value = "student_id") Long studentId){
        return ResponseEntity.ok(transactionService.getStudentChangedMoney(studentId));
    }


    @GetMapping("/salary/{student_id}")
    @Operation(description = "월급 지급 태그 선택시 자동으로 해당 학생의 월급 표기")
    public ResponseEntity<StudentSalaryResponseDto> getStudentSalary(@PathVariable(value = "student_id") Long studentId){
        return ResponseEntity.ok(studentService.getStudentSalary(studentId));
    }

}
