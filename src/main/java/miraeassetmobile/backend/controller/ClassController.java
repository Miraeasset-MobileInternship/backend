package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import miraeassetmobile.backend.domain.dto.classes.ClassAccountResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentJobDto;
import miraeassetmobile.backend.domain.dto.students.StudentTransferSelectorResponseDto;
import miraeassetmobile.backend.domain.entity.Job;
import miraeassetmobile.backend.service.ClassService;
import miraeassetmobile.backend.service.JobService;
import miraeassetmobile.backend.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/class")
@RestController
public class ClassController {

    JobService jobService;
    StudentService studentService;

    ClassService classService;

    public ClassController(ClassService classService, JobService jobService, StudentService studentService){
        this.jobService =jobService;
        this.studentService =studentService;
        this.classService = classService;
    }




    //선생님 직업조회 페이지 -> 해당 학급에서 가질 수 있는 모든 직업을 조회
    @GetMapping("/{class_id}/job/all")
    @Operation(description = "해당 학급에서 생성한 모든 직업과 공통 직업을 조회, 선생님 - 직업 변경 페이지(selector) 및 직업 조회 페이지")
    public ResponseEntity<List<Job>> jobListByClass(@PathVariable(value = "class_id")Long classId){
        return ResponseEntity.ok(jobService.getJobListByClass(classId));
    }



    //반아이들 전체의 직업조회
    @GetMapping("/{class_id}/student/job/all")
    @Operation(description = "해당 학급의 아이들과 아이들이 가진 직업 현황을 조회, 선생님 - 직업변경 페이지 / 학생 - 친구들의 직업")
    public ResponseEntity<List<StudentJobDto>> studentJobListByClass(@PathVariable(value = "class_id")Long classId){
        return ResponseEntity.ok(jobService.getAllStduentJobList(classId));
    }


    @GetMapping("/{class_id}/student-selector")
    @Operation(description = "해당 학급의 모든 아이들의 번호,이름을 제공(O번 OOO형태 포함), 학생 - 특수 업무 수행 - 출금/입금 대상 selector")
    public ResponseEntity<List<StudentTransferSelectorResponseDto>> getStudentList(@PathVariable(value = "class_id") Long classId){
        return ResponseEntity.ok(studentService.getStudentSelectorList(classId));
    }


    @GetMapping("/{class_id}/account")
    @Operation(description = "현 학급의 국고 상태 정보 제공 , 국고 페이지 - 국가명, 잔고, 국가화폐 단위")
    public ResponseEntity<ClassAccountResponseDto> getClassAccountInfo(@PathVariable(value = "class_id") Long classId){
        return ResponseEntity.ok(classService.getClassAccountInfo(classId));
    }


}
