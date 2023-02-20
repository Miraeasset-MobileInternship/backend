package miraeassetmobile.backend.controller;

import miraeassetmobile.backend.domain.dto.jobs.JobListDto;
import miraeassetmobile.backend.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


//pagenation을 위한 import


@RequestMapping("/api/job")
@RestController
public class JobController {

    private JobService jobService;

    public JobController(JobService jobService){
        this.jobService=jobService;
    }



    //선생님 직업조회 페이지 -> 해당 학급에 모든 직업을 조회
    //특정 클래스에
    @GetMapping("/class")
    public ResponseEntity<JobListDto> jobListByClass(@RequestParam(value = "class_id")int classId,
                                                     @RequestParam(value="page", defaultValue = "0") int page){


        return ResponseEntity.ok(jobService.getJobListByClass(classId,page));

    }


}
