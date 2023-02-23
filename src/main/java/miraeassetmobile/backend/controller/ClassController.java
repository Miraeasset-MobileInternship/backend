package miraeassetmobile.backend.controller;

import miraeassetmobile.backend.domain.dto.students.StudentJobListResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentTransferSelectorResponseDto;
import miraeassetmobile.backend.domain.entity.Job;
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

    public ClassController(JobService jobService, StudentService studentService){
        this.jobService =jobService;
        this.studentService =studentService;
    }




    //선생님 직업조회 페이지 -> 해당 학급에서 가질 수 있는 모든 직업을 조회
    @GetMapping("/{class_id}/job/all")
    public ResponseEntity<List<Job>> jobListByClass(@PathVariable(value = "class_id")Long classId){
        return ResponseEntity.ok(jobService.getJobListByClass(classId));
    }



    //반아이들 전체의 직업조회
    @GetMapping("/{class_id}/student/job/all")
    public ResponseEntity<List<StudentJobListResponseDto>> studentJobListByClass(@PathVariable(value = "class_id")Long classId){
        return ResponseEntity.ok(jobService.getAllStduentJobList(classId));
    }


    @GetMapping("/{class_id}/student-selector")
    public ResponseEntity<List<StudentTransferSelectorResponseDto>> getStudentList(@PathVariable(value = "class_id") Long classId){
        return ResponseEntity.ok(studentService.getStudentSelectorList(classId));
    }


}
