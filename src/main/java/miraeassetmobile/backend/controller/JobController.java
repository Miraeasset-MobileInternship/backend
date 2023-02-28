package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import miraeassetmobile.backend.domain.dto.jobs.JobCreateRequestDto;
import miraeassetmobile.backend.domain.dto.jobs.JobDto;
import miraeassetmobile.backend.domain.dto.jobs.StudentJobUpdateRequestDto;
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
    @Operation(description = "해당 직업의 상세 정보를 조회, 선생님 - 직업 수정 화면")
    public ResponseEntity<JobDto> getJobInfo(@PathVariable(value = "job_id") Long jobId){
        return ResponseEntity.ok(jobService.getJobInfo(jobId));
    }



    //특정 직업 삭제
    @DeleteMapping("/{job_id}/delete")
    @Operation(description = "해당 id의 직업을 삭제, 선생님 - 직업 삭제 기능")
    public ResponseEntity<Void> deleteJob(@PathVariable(value = "job_id") Long jobId){
        jobService.deleteJob(jobId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/create")
    @Operation(description = "새로운 직업을 생성, 선생님 - 직업 생성 화면")
    public ResponseEntity createJob(@RequestBody @Valid JobCreateRequestDto jobInfo){

        /*
        @Valid : 유효한 JobCreateDto객체인지 검사해준다.
        httpstatus 201 CREATED URI를 같이 RETURN한다
         */
        return ResponseEntity.created(jobService.createJob(jobInfo)).build(); //id를 같이 반환하기


    }


    @PutMapping("/update/all")
    @Operation(description = "아이들의 현 직업을 변경(단체), 선생님 - 아이들 직업 부여/수정(전체)")
    public ResponseEntity updateJobStudentAll(@RequestBody @Valid List<StudentJobUpdateRequestDto> jobList){
        jobService.updateAllStudentJob(jobList);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/update")
    @Operation(description = "아이들의 현 직업을 변경(개인), 선생님 - 아이들 직업 수정/부여(개인)")
    public ResponseEntity updateJobStudent(@RequestBody @Valid StudentJobUpdateRequestDto studentJobUpdateRequestDto){
        return ResponseEntity.created(jobService.updateStudentJob(studentJobUpdateRequestDto)).build(); //id를 같이 반환하기
    }


}
