package miraeassetmobile.backend.controller;

import miraeassetmobile.backend.domain.dto.jobs.JobDto;
import miraeassetmobile.backend.domain.dto.jobs.JobListDto;
import miraeassetmobile.backend.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//pagenation을 위한 import


@RequestMapping("/api/job")
@RestController
public class JobController {

    private JobService jobService;

    public JobController(JobService jobService){
        this.jobService=jobService;
    }



    //선생님 직업조회 페이지 -> 해당 학급에 모든 직업을 조회
    //특정 학급에서 생성된 전체 직업 과 공통 직업에 대한 정보를 page에 따라 10개씩 조회
    @GetMapping("/class/{class_id}")
    public ResponseEntity<JobListDto> jobListByClass(@PathVariable(value = "class_id")Long classId){
        return ResponseEntity.ok(jobService.getJobListByClass(classId));
    }


    //job id를 통한 직업조회
    //특정 직업의 정보를 넘겨줌 (직업 수정 화면 등등)
    @GetMapping("/{job_id}")
    public ResponseEntity<JobDto> getJobInfo(@PathVariable(value = "job_id") Long jobId){
        return ResponseEntity.ok(jobService.getJobInfo(jobId));
    }


    @DeleteMapping("/{job_id}/delete")
    public ResponseEntity<Void> deleteJob(@PathVariable(value = "job_id") Long jobId){
        jobService.deleteJob(jobId);
        return ResponseEntity.ok().build();
    }


}
