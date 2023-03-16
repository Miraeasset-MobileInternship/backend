package miraeassetmobile.backend.v1.controller;

import io.swagger.v3.oas.annotations.Operation;

import miraeassetmobile.backend.domain.BanklassResponseEntity;

import miraeassetmobile.backend.service.ClassService;
import miraeassetmobile.backend.service.JobService;
import miraeassetmobile.backend.service.StudentService;
import miraeassetmobile.backend.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    @Operation(description = "해당 학급에서 생성한 모든 직업과 공통 직업을 조회, 선생님 - 직업 변경 페이지(selector) 및 직업 조회 페이지")
    public ResponseEntity<BanklassResponseEntity> jobListByClass(@PathVariable(value = "class_id")Long classId){
        return ResponseEntity.ok(jobService.getJobListByClass(classId));
    }


    //반아이들 전체의 직업조회
    @GetMapping("/{class_id}/student/job/all")
    @Operation(description = "해당 학급의 아이들과 아이들이 가진 직업 현황을 조회, 선생님 - 직업변경 페이지 / 학생 - 친구들의 직업")
    public ResponseEntity<BanklassResponseEntity> studentJobListByClass(@PathVariable(value = "class_id")Long classId){
        return ResponseEntity.ok(jobService.getAllStudentJobList(classId));
    }




    @GetMapping("/{class_id}/student-selector")
    @Operation(description = "해당 학급의 모든 아이들의 번호,이름을 제공(O번 OOO형태 포함), 학생 - 특수 업무 수행 - 출금/입금 대상 selector")
    public ResponseEntity<BanklassResponseEntity> getStudentList(@PathVariable(value = "class_id") Long classId){
        return ResponseEntity.ok(studentService.getStudentSelectorList(classId));
    }



    @GetMapping("/{class_id}/account")
    @Operation(description = "현 학급의 국고 상태 정보 제공 , 국고 페이지 - 국가명, 잔고, 국가화폐 단위")
    public ResponseEntity<BanklassResponseEntity> getClassAccountInfo(@PathVariable(value = "class_id") Long classId){
        return ResponseEntity.ok(classService.getClassAccountInfo(classId));
    }


    //이전날 대비 변동가격
    @GetMapping("/{class_id}/change")
    @Operation(description = "국고의 계좌금액 : 전날대비 변동가격 - 국과 화면 국고 계좌")
    public ResponseEntity<BanklassResponseEntity> getClassChangeMoney(@PathVariable(value = "class_id") Long classId){
        return ResponseEntity.ok(transactionService.getClassChangedMoney(classId));
    }
//
//
//    @PostMapping("/create")
//    @Operation(description = "학급 생성 API : 선생님 기능")
//    public ResponseEntity<MoneyChangeResponseDto> createClass(@RequestBody @Valid ClassCreateRequestDto classCreateRequestDto){
//        return ResponseEntity.created(classService.createClass(classCreateRequestDto)).build();
//    }
//
//    //학급 초대 코드 복사 ( 만료되었으면 자동 재발급 )
//    @GetMapping("/{class_id}/invitation-code")
//    @Operation(description = "학급 초대 코드 제공 (복사하기)")
//    public ResponseEntity<ClassInvitationCodeResponseDto> getClassInvitationCode(@PathVariable(value = "class_id") Long classId){
//        return ResponseEntity.ok(classService.getClassInvitationCode(classId));
//    }
//
//
//    //학급 초대 코드 복사
////    @PostMapping("/{class_id}/reissue-code")
////    @Operation(description = "학급 초대 코드 재발급")
////    public ResponseEntity reissueClassInvitationCode(@PathVariable(value = "class_id") Long classId){
////        classService.reissueInvitationCode(classId);
////        return ResponseEntity.ok().build();
////    }
//
//    @GetMapping("/check/invitation-code")
//    @Operation(description = "유효한 초대 코드인지 검증 후 학급 정보를 보내줌")
//    public ResponseEntity<ClassValidInvitationResponseDto> checkInvitationCode(@RequestParam(value = "invitation_code") String invitationCode){
//        return ResponseEntity.ok(classService.checkInvitationCode(invitationCode));
//    }
//
//
//
}
