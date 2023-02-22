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
    private StudentService studentService;

    public JobController(JobService jobService, StudentService studentService){
        this.jobService=jobService;
        this.studentService=studentService;
    }



    //선생님 직업조회 페이지 -> 해당 학급에서 가질 수 있는 모든 직업을 조회
    @GetMapping("/class/{class_id}")
    public ResponseEntity<List<Job>> jobListByClass(@PathVariable(value = "class_id")Long classId){
        return ResponseEntity.ok(jobService.getJobListByClass(classId));
    }



    //반아이들 전체의 직업조회
    @GetMapping("/class/{class_id}/student/all")
    public ResponseEntity<List<StudentJobDto>> studentJobListByClass(@PathVariable(value = "class_id")Long classId){
        return ResponseEntity.ok(jobService.getAllStduentJobList(classId));
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
