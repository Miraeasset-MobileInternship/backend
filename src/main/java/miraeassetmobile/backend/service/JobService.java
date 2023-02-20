package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.jobs.JobListDto;
import miraeassetmobile.backend.domain.entity.Job;
import miraeassetmobile.backend.repository.JobRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class JobService {


    private final JobRepository jobRepository;


    public JobService(JobRepository jobRepository){
        this.jobRepository = jobRepository;
    }




    /*
    공통직업(classId=1로 등록)을 포함한 직업을 page에 따라 10개씩 반환하는 함수
     */
    public JobListDto getJobListByClass(int classId, int page){

        int pageSize = 10;

        //총 직업의 수
        int totalNum = jobRepository.countByClassId(classId) + jobRepository.countByClassId(1); // 1로 지정된 것은 공통 직업임

        //총 페이지의 수
        int totalPageNum = (int) Math.ceil(totalNum/pageSize);

        /*
        공통 직업의 수가 원하는 page의 결과값 보다 크거나 같은 경우
        -> 결과로 전부다 공통 직업만 내보내야함
         */

        if(jobRepository.countByClassId(1) >= (page+1) * 10){

            //공통직업을 구함
            Pageable paging = PageRequest.of(page,pageSize); //한페이지당 10개 반환
            List<Job> jobs = jobRepository.findByClassId(1, paging).getContent();

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
            List<Job> firstJobs = jobRepository.findByClassId(1, firstPaging).getContent();


            //공통직업이 포함되었으므로, 그만큼 페이지가 적어져야함
            int secondPage = page - jobRepository.countByClassId(1)/pageSize;

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



}
