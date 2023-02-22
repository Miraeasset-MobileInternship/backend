package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.jobs.JobCreateDto;
import miraeassetmobile.backend.domain.dto.jobs.JobDto;
import miraeassetmobile.backend.domain.dto.jobs.JobListDto;
import miraeassetmobile.backend.domain.dto.students.StudentJobDto;
import miraeassetmobile.backend.domain.entity.Job;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.error.exception.AlreadyExistException;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.error.exception.UnavailableException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.JobRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Service
public class JobService {


    private final JobRepository jobRepository;
    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;


    public JobService(JobRepository jobRepository, ClassRepository classRepository, StudentRepository studentRepository){
        this.jobRepository = jobRepository;
        this.classRepository = classRepository;
        this.studentRepository = studentRepository;
    }



    /*
    공통직업(classId=1로 등록)을 포함한 직업을 page에 따라 10개씩 반환하는 함수
     */
    public List<Job> getJobListByClass(Long classId){

        //존재하는 학급인지
        isExistClass(classId);

        if(classId == 1){ //공통직업을 조회한 경우


            List<Job> publicJob = jobRepository.findByClassId(1L); //공통직업리스트


            return publicJob;

        }else{ //특정 학급의 직업을 조회한 경우


            List<Job> publicJob = jobRepository.findByClassId(1L); //공통직업리스트

            List<Job> localJobs = jobRepository.findByClassId(classId); //조회된 학급의 리스트


            List<Job> jobs = new ArrayList<Job>();

            //두 리스트를 합치기
            jobs.addAll(publicJob);
            jobs.addAll(localJobs);

            return jobs;
        }

    }


    //특정 학급의 아이들의 전체 직업과 정보를 넘김
    public List<StudentJobDto> getAllStduentJobList(Long classId){
        List<Student> students = studentRepository.findByClassId(classId);

        List<StudentJobDto> studentJobs = new ArrayList<StudentJobDto>();

        for (Student student : students) {

            Job job = jobRepository.findById(student.getJobId()).get();

            studentJobs.add(StudentJobDto.builder()
                    .id(student.getId())
                    .number(student.getNumber())
                    .studentName(student.getName())
                    .jobId(student.getJobId())
                    .jobTitle(job.getTitle())
                    .build());

        }

        return studentJobs;

    }



    //특정 job의 정보를 조회함
    public JobDto getJobInfo(Long id){


        //존재하는 직업인지
        isExistJob(id);


        Job job = jobRepository.findById(id).get();

        //필요한 것만 dto에 담아서 전달
        return JobDto.builder().id(job.getId())
                .title(job.getTitle())
                .monthlySalary(job.getMonthlySalary())
                .creditLimit(job.getCreditLimit())
                .detail(job.getDetail())
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


   //신규직업등록
    public URI createJob(JobCreateDto jobCreateDto){

        //존재하는 학급인지
        isExistClass(jobCreateDto.getClassId());

        //등록가능한 직업명인지 확인
        validateJobNameInClass(jobCreateDto.getClassId(), jobCreateDto.getJobTitle());


        //직업등록
        Job newJob = jobCreateDto.toJob(jobCreateDto.getClassId(), jobCreateDto.getJobTitle(), jobCreateDto.getDetail(), jobCreateDto.getMonthlySalary(), jobCreateDto.isWithdrawStudent(), jobCreateDto.isWithdrawClass()); //save에서 에러난다


        Job j = jobRepository.save(newJob);


        return createJobUri(j.getId()); //등록된 직업에 대해 URI를 같이 반환함

    }


    //새로 생성되거나 수정된 job의 id를 포함한 URI만들기
    public URI createJobUri(Long jobId){
        URI uri = UriComponentsBuilder.newInstance()
//                .scheme("https")
//                .host("m-crew.iptime.org")
//                .port(8001)
                .scheme("http")
                .host("localhost")
                .port(8080)
                .path("/api/job/" + jobId)
                .build()
                .toUri(); //UriComponents into URI

        return uri;

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

    public void isExistStudent(Long studentId){

        if(!studentRepository.existsById(studentId)){
            throw new NotExistException(ErrorCode.NOT_EXIST_STUDENT);
        }
    }

    public void isExistClass(Long classId){
        if(!classRepository.existsById(classId)){
            throw new NotExistException(ErrorCode.NOT_EXIST_CLASS);
        }
    }


    public void validateJobNameInClass(Long classId, String title){

        if(jobRepository.existsByClassIdAndTitle(classId, title)){ //해당 학급에 같은 이름의 직업이 이미 존재함
            throw new AlreadyExistException(ErrorCode.ALREADY_EXIST_JOB);
        }

    }


}
