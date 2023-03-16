package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.jobs.JobCreateRequestDto;
import miraeassetmobile.backend.domain.dto.jobs.StudentJobUpdateRequestDto;
import miraeassetmobile.backend.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


//pagenation을 위한 import


@RequestMapping("/api/job")
@RestController
public class JobController {

    private JobService jobService;


    public JobController(JobService jobService){
        this.jobService=jobService;
    }






    //job id를 통한 직업조회
    //특정 직업의 정보를 넘겨줌 (직업 수정 화면 등등)
    @GetMapping("/{job_id}")
    @Operation(description = "해당 직업의 상세 정보를 조회, 선생님 - 직업 수정 화면")
    public ResponseEntity<BanklassResponseEntity> getJobInfo(@PathVariable(value = "job_id") Long jobId){
        return ResponseEntity.ok(jobService.getJobInfo(jobId));
    }



    //특정 직업 삭제
    @DeleteMapping("/{job_id}/delete")
    @Operation(description = "해당 id의 직업을 삭제, 선생님 - 직업 삭제 기능")
    public ResponseEntity<BanklassResponseEntity> deleteJob(@PathVariable(value = "job_id") Long jobId){
        return ResponseEntity.ok(jobService.deleteJob(jobId));
    }


    @PostMapping("/create")
    @Operation(description = "새로운 직업을 생성, 선생님 - 직업 생성 화면")
    public ResponseEntity<BanklassResponseEntity> createJob(@RequestBody @Valid JobCreateRequestDto jobInfo){

        /*
        @Valid : 유효한 JobCreateDto객체인지 검사해준다.
        httpstatus 201 CREATED URI를 같이 RETURN한다
         */
        return ResponseEntity.ok(jobService.createJob(jobInfo)); //id를 같이 반환하기


    }


    @PutMapping("/update/all")
    @Operation(description = "아이들의 현 직업을 변경(단체), 선생님 - 아이들 직업 부여/수정(전체)")
    public ResponseEntity<BanklassResponseEntity> updateJobStudentAll(@RequestBody @Valid List<StudentJobUpdateRequestDto> jobList){
        return ResponseEntity.ok(jobService.updateAllStudentJob(jobList));
    }


    @PutMapping("/update")
    @Operation(description = "아이들의 현 직업을 변경(개인), 선생님 - 아이들 직업 수정/부여(개인)")
    public ResponseEntity<BanklassResponseEntity> updateJobStudent(@RequestBody @Valid StudentJobUpdateRequestDto studentJobUpdateRequestDto){
        return ResponseEntity.ok(jobService.updateStudentJob(studentJobUpdateRequestDto)); //id를 같이 반환하기
    }


}
