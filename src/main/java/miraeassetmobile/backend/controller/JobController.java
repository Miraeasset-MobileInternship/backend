package miraeassetmobile.backend.controller;

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
    @GetMapping("/class")
    public ResponseEntity<JobListDto> jobListByClass(@RequestParam(value = "class_id")int classId,
                                                     @RequestParam(value="page", defaultValue = "0") int page){

        return ResponseEntity.ok(jobService.getJobListByClass(classId,page));
    }


//    @DeleteMapping("/deletejob")
//    public


    @DeleteMapping("/{job_id}/delete")
    public ResponseEntity<Void> deleteJob(@PathVariable(value = "job_id") Long jobId){
        jobService.deleteJob(jobId);
        return ResponseEntity.ok().build();
    }


}
