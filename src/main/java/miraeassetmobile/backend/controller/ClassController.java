package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;


import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import miraeassetmobile.backend.domain.BanklassResponseEntity;

import miraeassetmobile.backend.domain.dto.CreatedUriDto;
import miraeassetmobile.backend.domain.dto.auth.AccessTokenInfo;
import miraeassetmobile.backend.domain.dto.classes.ClassAccountResponseDto;
import miraeassetmobile.backend.domain.dto.classes.ClassCreateRequestDto;
import miraeassetmobile.backend.domain.dto.classes.ClassCurrencyResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentJobListResponseDto;
import miraeassetmobile.backend.domain.dto.classes.ClassInvitationCodeResponseDto;
import miraeassetmobile.backend.domain.dto.classes.ClassValidInvitationResponseDto;
import miraeassetmobile.backend.domain.dto.jobs.ClassJobListResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentJobDto;
import miraeassetmobile.backend.domain.dto.students.StudentTransferSelectorDto;
import miraeassetmobile.backend.domain.dto.transactions.MoneyChangeResponseDto;

import miraeassetmobile.backend.domain.dto.users.ClassEnterStudentResponseDto;
import miraeassetmobile.backend.service.ClassService;
import miraeassetmobile.backend.service.JobService;
import miraeassetmobile.backend.service.StudentService;
import miraeassetmobile.backend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/class")
@RestController
public class ClassController {

    JobService jobService;
    StudentService studentService;

    ClassService classService;
    TransactionService transactionService;

    public ClassController(StudentService studentService, ClassService classService, TransactionService transactionService, JobService jobService){
        this.studentService =studentService;
        this.classService = classService;
        this.transactionService = transactionService;
        this.jobService =jobService;
    }




    //선생님 직업조회 페이지 -> 해당 학급에서 가질 수 있는 모든 직업을 조회
    @GetMapping("/{class_id}/job/all")
//    @Operation(description = "해당 학급에서 생성한 모든 직업과 공통 직업을 조회, 선생님 - 직업 변경 페이지(selector) 및 직업 조회 페이지")
    @Operation(summary = "학급 공통/생성 직업 조회", description = "직업 변경 페이지(selector) 및 직업 조회 페이지",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = ClassJobListResponseDto.class))),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> jobListByClass(@PathVariable(value = "class_id")Long classId){
        return ResponseEntity.ok(jobService.getJobListByClass(classId));
    }


    //반아이들 전체의 직업조회
    @GetMapping("/{class_id}/student/job/all")
    @Operation(summary = "아이들의 직업 현황", description = "해당 학급의 아이들과 아이들이 가진 직업 현황을 조회, 선생님 - 직업변경 페이지 / 학생 - 친구들의 직업",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success",  content = @Content(schema = @Schema(implementation = StudentJobListResponseDto.class))),

                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content ),
                    @ApiResponse(responseCode = "E405", description = "존재하지 않는 직업", content = @Content ),
                    @ApiResponse(responseCode = "E403", description = "존재하지 않는 유저", content = @Content ),
                    @ApiResponse(responseCode = "E409", description = "존재하지 않는 프로필 이미지", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> studentJobListByClass(@PathVariable(value = "class_id")Long classId){
        return ResponseEntity.ok(jobService.getAllStudentJobList(classId));
    }




    @GetMapping("/{class_id}/student-selector")
//    @Operation(description = "해당 학급의 모든 아이들의 번호,이름을 제공(O번 OOO형태 포함), 학생 - 특수 업무 수행 - 출금/입금 대상 selector")
    @Operation(summary = "이체/지급 학생 selector", description = "해당 학급의 모든 아이들의 번호,이름을 제공(O번 OOO형태 포함)",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(array = @ArraySchema(schema = @Schema(implementation = StudentTransferSelectorDto.class)))),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content ),
                    @ApiResponse(responseCode = "E403", description = "존재하지 않는 유저", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> getStudentList(@PathVariable(value = "class_id") Long classId){
        return ResponseEntity.ok(studentService.getStudentSelectorList(classId));
    }



    @GetMapping("/{class_id}/account")
    //@Operation(description = "현 학급의 국고 상태 정보 제공 , 국고 페이지 - 국가명, 잔고, 국가화폐 단위")
    @Operation(summary = "국고 계좌 상태 정보", description = "현 학급의 국고 상태 정보 제공 , 국고 페이지 - 국가명, 잔고, 국가화폐 단위",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = ClassAccountResponseDto.class))),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content ),
                    @ApiResponse(responseCode = "E403", description = "존재하지 않는 유저", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> getClassAccountInfo(@PathVariable(value = "class_id") Long classId){
        return ResponseEntity.ok(classService.getClassAccountInfo(classId));
    }


    //이전날 대비 변동가격
    @GetMapping("/{class_id}/change")
//    @Operation(description = "국고의 계좌금액 : 전날대비 변동가격 - 국과 화면 국고 계좌")
    @Operation(summary = "국고 : 직전거래 대비 변동", description = "국고의 계좌금액 : 전날대비 변동가격 - 국과 화면 국고 계좌",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = MoneyChangeResponseDto.class))),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content ),
                    @ApiResponse(responseCode = "E407", description = "금일을 제외한 거래내역이 존재하지 않음", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> getClassChangeMoney(@PathVariable(value = "class_id") Long classId){
        return ResponseEntity.ok(transactionService.getClassChangedMoney(classId));
    }


    @PostMapping("/create")
//    @Operation(description = "학급 생성 API : 선생님 기능")
    @Operation(summary = "학급 생성", description = "학급 생성(선생님 기능)",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = CreatedUriDto.class))),
                    @ApiResponse(responseCode = "E604", description = "이미 해당 년도에 해당 학교/학년/반 정보로 생성된 학급이 존재함", content = @Content ),
                    @ApiResponse(responseCode = "E605", description = "이미 해당 학교에서 사용중인 나라 이름", content = @Content ),
                    @ApiResponse(responseCode = "E803", description = "학급 생성 과정에서 db저장 중 발생한 에러", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> createClass(@RequestBody @Valid ClassCreateRequestDto classCreateRequestDto){
        return ResponseEntity.ok(classService.createClass(classCreateRequestDto));
    }

    //학급 초대 코드 복사 ( 만료되었으면 자동 재발급 )
    @GetMapping("/{class_id}/invitation-code")
//    @Operation(description = "학급 초대 코드 제공 (복사하기)")
    @Operation(summary = "학급 초대코드 복사", description = "학급 초대 코드 얻기",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = ClassInvitationCodeResponseDto.class))),
            })
    public ResponseEntity<BanklassResponseEntity> getClassInvitationCode(@PathVariable(value = "class_id") Long classId){
        return ResponseEntity.ok(classService.getClassInvitationCode(classId));
    }


    //학급 초대 코드 복사
//    @PostMapping("/{class_id}/reissue-code")
//    @Operation(description = "학급 초대 코드 재발급")
//    public ResponseEntity reissueClassInvitationCode(@PathVariable(value = "class_id") Long classId){
//        classService.reissueInvitationCode(classId);
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/check/invitation-code")
//    @Operation(description = "유효한 초대 코드인지 검증 후 학급 정보를 보내줌")
    @Operation(summary = "초대코드 검증", description = "유효한 초대 코드인지 검증 후 학급 정보를 보내줌",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = ClassValidInvitationResponseDto.class))),
                    @ApiResponse(responseCode = "E102", description = "올바르지 않거나 만료된 코드", content = @Content ),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content ),
                    @ApiResponse(responseCode = "E403", description = "존재하지 않는 유저", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> checkInvitationCode(@RequestParam(value = "invitation_code") String invitationCode){
        return ResponseEntity.ok(classService.checkInvitationCode(invitationCode));
    }


    @GetMapping("/{class_id}/currency")
    @Operation(summary = "학급 화폐 단위 전송", description = "",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = ClassCurrencyResponseDto.class))),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> getClassCurrency(@PathVariable(value = "class_id") Long classId){
        return ResponseEntity.ok(classService.getClassCurrency(classId));
    }



    @GetMapping("/enter-class/{class_id}")
    @Operation(summary = "학생 - 학급에 입장", description = "특정 유저가 학급에 입장하는 경우 해당 학급에서 사용할 studentId를 반환하는 api",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = ClassEnterStudentResponseDto.class))),
                    @ApiResponse(responseCode = "E403", description = "존재하지 않는 유저", content = @Content ),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content ),
                    @ApiResponse(responseCode = "E709", description = "해당 유저가 해당 학급에 학생이 아닌 경우", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> enterClass(@PathVariable(value = "class_id") Long classId,
                                                             @RequestParam(value = "user_id") Long userId){
        return ResponseEntity.ok(classService.getInfoToEnterClass(classId,userId));
    }

}
