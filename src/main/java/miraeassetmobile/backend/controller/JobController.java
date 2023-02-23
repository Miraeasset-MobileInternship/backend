package miraeassetmobile.backend.controller;

import miraeassetmobile.backend.domain.dto.jobs.JobCreateRequestDto;
import miraeassetmobile.backend.domain.dto.jobs.JobDto;
import miraeassetmobile.backend.domain.dto.jobs.StudentJobUpdateRequestDto;
import miraeassetmobile.backend.domain.dto.students.StudentJobListResponseDto;
import miraeassetmobile.backend.domain.entity.Job;
import miraeassetmobile.backend.service.JobService;
import miraeassetmobile.backend.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


//pagenation을 위한 import


@RequestMapping("/api/job")
@RestController
public class JobController {

    private JobService jobService;
    private StudentService studentService;

    public JobController(JobService jobService, StudentService studentService){
        this.jobService=jobService;
        this.studentService=studentService;
    }






    //job id를 통한 직업조회
    //특정 직업의 정보를 넘겨줌 (직업 수정 화면 등등)
    @GetMapping("/{job_id}")
    public ResponseEntity<JobDto> getJobInfo(@PathVariable(value = "job_id") Long jobId){
        return ResponseEntity.ok(jobService.getJobInfo(jobId));
    }



    //특정 직업 삭제
    @DeleteMapping("/{job_id}/delete")
    public ResponseEntity<Void> deleteJob(@PathVariable(value = "job_id") Long jobId){
        jobService.deleteJob(jobId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/create")
    public ResponseEntity createJob(@RequestBody @Valid JobCreateRequestDto jobInfo){

        /*
        @Valid : 유효한 JobCreateDto객체인지 검사해준다.
        httpstatus 201 CREATED URI를 같이 RETURN한다
         */
        return ResponseEntity.created(jobService.createJob(jobInfo)).build(); //id를 같이 반환하기


    }


    @PutMapping("/update/all")
    public ResponseEntity updateJobStudentAll(@RequestBody @Valid List<StudentJobUpdateRequestDto> jobList){
        jobService.updateAllStudentJob(jobList);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/update/{student_id}")
    public ResponseEntity updateJobStudent(@RequestBody @Valid StudentJobUpdateRequestDto studentJobUpdateRequestDto,
                                           @PathVariable(value = "student_id") Long studentId){
        return ResponseEntity.created(jobService.updateStudentJob(studentJobUpdateRequestDto)).build(); //id를 같이 반환하기
    }


}
