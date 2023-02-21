package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.jobs.JobDto;
import miraeassetmobile.backend.domain.dto.jobs.JobListDto;
import miraeassetmobile.backend.domain.entity.Job;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.error.exception.UnavailableException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class JobService {


    private final JobRepository jobRepository;
    private final ClassRepository classRepository;


    public JobService(JobRepository jobRepository, ClassRepository classRepository){
        this.jobRepository = jobRepository;
        this.classRepository = classRepository;
    }



    /*
    공통직업(classId=1로 등록)을 포함한 직업을 page에 따라 10개씩 반환하는 함수
     */
    public JobListDto getJobListByClass(Long classId){

        //존재하는 학급인지
        isExistClass(classId);

        if(classId == 1){ //공통직업을 조회한 경우

            //총 직업의 수
            int totalNum = jobRepository.countByClassId(1L);


            List<Job> publicJob = jobRepository.findByClassId(1L); //공통직업리스트

            //builder로 내보내기
            JobListDto jobData = JobListDto.builder()
                    .totalNum(totalNum)
                    .jobs(publicJob)
                    .build();

            return jobData;

        }else{ //특정 학급의 직업을 조회한 경우

            //총 직업의 수
            int totalNum = jobRepository.countByClassId(classId) + jobRepository.countByClassId(1L);

            List<Job> publicJob = jobRepository.findByClassId(1L); //공통직업리스트

            List<Job> localJobs = jobRepository.findByClassId(classId); //조회된 학급의 리스트


            List<Job> jobs = new ArrayList<Job>();

            //두 리스트를 합치기
            jobs.addAll(publicJob);
            jobs.addAll(localJobs);


            //합치기
            JobListDto jobData = JobListDto.builder()
                    .totalNum(totalNum)
                    .jobs(jobs)
                    .build();


            return jobData;
        }

    }



    //
    public JobDto getJobInfo(Long id){


        //존재하는 직업인지
        isExistJob(id);


        Job job = jobRepository.findById(id).get();

        //필요한 것만 dto에 담아서 전달
        return JobDto.builder().id(job.getId())
                .title(job.getTitle())
                .monthlySalary(job.getMonthlySalary())
                .detail(job.getDetail())
                .creditLimit(job.getCreditLimit())
                .isWithdrawClass(job.isWithdrawClass())
                .isWithdrawStudent(job.isWithdrawStudent())
                .isModifyCredit(job.isModifyCredit())
                .build();

    }

    public void deleteJob(Long id){

        //예외처리들
        //존재하는 직업인가
        isExistJob(id);
        //삭제 가능한 직업인가
        unavailableJobDelete(id);

        //삭제
        jobRepository.deleteById(id);

    }


    //필수직업 삭제 불가능
    public void unavailableJobDelete(Long jobId){
        if(jobRepository.findById(jobId).get().getClassId() == 1){ //master job인 경우
            throw new UnavailableException(ErrorCode.UNAVAILABLE_ACTION_DELETE_JOB); // 삭제 불가능한 것을 삭제하려고 한다.
        }
    }

    public void isExistJob(Long jobId){

        if(!jobRepository.existsById(jobId)){
            throw new NotExistException(ErrorCode.NOT_EXIST_JOB);
        }
    }

    public void isExistClass(Long classId){
        if(!classRepository.existsById(classId)){
            throw new NotExistException(ErrorCode.NOT_EXIST_CLASS);
        }
    }


}
