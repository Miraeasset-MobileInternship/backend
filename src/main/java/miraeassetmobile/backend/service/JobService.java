package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.jobs.JobDto;
import miraeassetmobile.backend.domain.dto.jobs.JobListDto;
import miraeassetmobile.backend.domain.entity.Job;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.error.exception.UnavailableException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.JobRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public JobListDto getJobListByClass(Long classId, int page){

        //존재하는 학급인지
        isExistClass(classId);


        int pageSize = 10;

        //총 직업의 수
        int totalNum = jobRepository.countByClassId(classId) + jobRepository.countByClassId(1L); // 1로 지정된 것은 공통 직업임

        //총 페이지의 수
        int totalPageNum = (int) Math.ceil(totalNum/pageSize);

        /*
        공통 직업의 수가 원하는 page의 결과값 보다 크거나 같은 경우
        -> 결과로 전부다 공통 직업만 내보내야함
         */

        if(jobRepository.countByClassId(1L) >= (page+1) * 10){

            //공통직업을 구함
            Pageable paging = PageRequest.of(page,pageSize); //한페이지당 10개 반환
            List<Job> jobs = jobRepository.findByClassId(1L, paging).getContent();

            JobListDto jobData = JobListDto.builder()
                    .totalNum(totalNum)
                    .totalPageNum(totalPageNum)
                    .jobs(jobs)
                    .build();

            return jobData;



        }else{
            /*
            더 작은 경우)
            pageSize 를 못채운 만큼은 classId학급에서 가져와야함
             */

            //공통 직업을 가져오기
            Pageable firstPaging = PageRequest.of(page,pageSize); //한페이지당 10개 반환(근데 10개 미만임)
            List<Job> firstJobs = jobRepository.findByClassId(1L, firstPaging).getContent();


            //공통직업이 포함되었으므로, 그만큼 페이지가 적어져야함
            int secondPage = page - jobRepository.countByClassId(1L)/pageSize;

            // 공통직업이 부족한 만큼 Job가져오기
            Pageable secondPaging = PageRequest.of(secondPage, pageSize-firstJobs.size()); // 부족한 갯수만큼 학급에서 가져오기
            List<Job> secondJobs = jobRepository.findByClassId(classId, secondPaging).getContent();

            List<Job> jobs = new ArrayList<Job>();

            jobs.addAll(firstJobs);
            jobs.addAll(secondJobs);

            //합치기
            JobListDto jobData = JobListDto.builder()
                    .totalNum(totalNum)
                    .totalPageNum(totalPageNum)
                    .jobs(jobs)
                    .build();

            return jobData;

        }


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
