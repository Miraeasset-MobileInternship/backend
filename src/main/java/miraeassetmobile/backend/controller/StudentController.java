package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.CreatedUriDto;
import miraeassetmobile.backend.domain.dto.jobs.JobDto;
import miraeassetmobile.backend.domain.dto.students.StudentAccountResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentClassJoinRequestDto;
import miraeassetmobile.backend.domain.dto.students.StudentJobResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentSalaryResponseDto;
import miraeassetmobile.backend.domain.dto.transactions.MoneyChangeResponseDto;
import miraeassetmobile.backend.service.StudentService;
import miraeassetmobile.backend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
//    @Operation(description = "학생의 계좌 정보를 조회(보유 금액등) , 학생 - 홈 화면 본인 계좌 카드 부분")
    @Operation(summary = "학생 계좌 조회", description = "학생의 계좌 정보를 조회(보유 금액등) , 학생 - 홈 화면 본인 계좌 카드 부분",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = StudentAccountResponseDto.class))),
            })
    public ResponseEntity<BanklassResponseEntity>  getStudentAccountInfo(@PathVariable(value = "student_id") Long id){

        return ResponseEntity.ok(studentService.getStudentAccountInfo(id));

    }


    @GetMapping("/{student_id}/job")
//    @Operation(description = "학생의 직업 카드에 필요한 정보 조회, 학생 - 직업 화면 학생 본인 직업카드")
    @Operation(summary = "학생 직업 정보 조회", description = "학생의 직업 카드에 필요한 정보 조회, 학생 - 직업 화면 학생 본인 직업카드",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = StudentJobResponseDto.class))),
            })
    public ResponseEntity<BanklassResponseEntity> getStudentJobInfo(@PathVariable(value = "student_id") Long studentId){
        return ResponseEntity.ok(studentService.getStudentJobInfo(studentId));
    }


    //이전날 대비 변동가격
    @GetMapping("/{student_id}/change")
//    @Operation(description = "학생의 계좌금액 : 전날대비 변동가격 - 직업 화면 학생 본인 계좌")
    @Operation(summary = "학생 - 직전거래 대비 변동", description = "학생의 계좌금액 : 전날대비 변동가격 - 직업 화면 학생 본인 계좌",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = MoneyChangeResponseDto.class))),
            })
    public ResponseEntity<BanklassResponseEntity> getStudentChangeMoney(@PathVariable(value = "student_id") Long studentId){
        return ResponseEntity.ok(transactionService.getStudentChangedMoney(studentId));
    }


    @GetMapping("/salary/{student_id}")
//    @Operation(description = "월급 지급 태그 선택시 자동으로 해당 학생의 월급 표기")
    @Operation(summary = "월급지급: 학생 월급 조회", description = "월급 지급 태그 선택시 자동으로 해당 학생의 월급 표기",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = StudentSalaryResponseDto.class))),
            })
    public ResponseEntity<BanklassResponseEntity> getStudentSalary(@PathVariable(value = "student_id") Long studentId){
        return ResponseEntity.ok(studentService.getStudentSalary(studentId));
    }



    @PostMapping("/join-class")
//    @Operation(description = "해당 학급에 가입시켜주는 API")
    @Operation(summary = "학급 가입", description = "초대코드를 통해 학생을 학급에 가입",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success - status: created", content = @Content(schema = @Schema(implementation = CreatedUriDto.class))),
            })
    public ResponseEntity<BanklassResponseEntity> joinClass(@RequestBody @Valid StudentClassJoinRequestDto studentClassJoinReqeustDto){
        return ResponseEntity.ok(studentService.createStudentInClass(studentClassJoinReqeustDto));
    }




}
