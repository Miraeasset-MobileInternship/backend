package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.jobs.JobCreateRequestDto;
import miraeassetmobile.backend.domain.dto.jobs.JobDto;
import miraeassetmobile.backend.domain.dto.jobs.StudentJobUpdateRequestDto;
import miraeassetmobile.backend.domain.dto.students.StudentJobDto;
import miraeassetmobile.backend.domain.entity.Job;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.UserInfo;
import miraeassetmobile.backend.domain.enums.UriTypes;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.repository.JobRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Service
public class JobService {


    private final ErrorService errorService;
    private final JobRepository jobRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;


    public JobService(UserRepository userRepository,ErrorService errorService,JobRepository jobRepository, StudentRepository studentRepository){
        this.errorService =errorService;
        this.jobRepository = jobRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }



    /*
    공통직업(classId=1로 등록)을 포함한 직업을 page에 따라 10개씩 반환하는 함수
     */
    public List<JobDto> getJobListByClass(Long classId){

        //존재하는 학급인지
        errorService.isExistClass(classId);

        if(classId == 1){ //공통직업을 조회한 경우


            List<Job> publicJob = jobRepository.findByClassId(1L); //공통직업리스트

            List<JobDto> jobLists = new ArrayList<>();

            for (Job j: publicJob) {

                jobLists.add(JobDto.builder()
                        .jobId(j.getId())
                        .title(j.getTitle())
                        .detail(j.getDetail())
                        .monthlySalary(j.getMonthlySalary())
                        .creditLimit(j.getCreditLimit())
                        .isWithdrawStudent(j.isWithdrawStudent())
                        .isWithdrawClass(j.isWithdrawClass())
                        .isModifyCredit(j.isModifyCredit())
                        .build());


            }


            return jobLists;

        }else{ //특정 학급의 직업을 조회한 경우


            List<Job> publicJob = jobRepository.findByClassId(1L); //공통직업리스트

            List<Job> localJobs = jobRepository.findByClassId(classId); //조회된 학급의 리스트


            List<Job> jobs = new ArrayList<Job>();

            //두 리스트를 합치기
            jobs.addAll(publicJob);
            jobs.addAll(localJobs);

            List<JobDto> jobLists = new ArrayList<>();

            for (Job j: jobs) {

                jobLists.add(JobDto.builder()
                        .jobId(j.getId())
                        .title(j.getTitle())
                        .detail(j.getDetail())
                        .monthlySalary(j.getMonthlySalary())
                        .creditLimit(j.getCreditLimit())
                        .isWithdrawStudent(j.isWithdrawStudent())
                        .isWithdrawClass(j.isWithdrawClass())
                        .isModifyCredit(j.isModifyCredit())
                        .build());


            }



            return jobLists;
        }

    }


    //특정 학급의 아이들의 전체 직업과 정보를 넘김
    public List<StudentJobDto> getAllStduentJobList(Long classId){

        //존재하는 학급인지
        errorService.isExistClass(classId);


        List<Student> students = studentRepository.findByClassId(classId);

        List<StudentJobDto> studentJobs = new ArrayList<StudentJobDto>();

        for (Student student : students) {

            //존재하지 않는 직업 에러
            Job job = jobRepository.findById(student.getJobId()).orElseThrow(()->new NotExistException(ErrorCode.NOT_EXIST_JOB));

            UserInfo u = userRepository.findById(student.getUserId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));

            studentJobs.add(StudentJobDto.builder()
                    .studentId(student.getId())
                    .number(student.getNumber())
                    .studentName(u.getName())
                    .jobId(student.getJobId())
                    .jobTitle(job.getTitle())
                    .build());

        }

        return studentJobs;

    }



    //특정 job의 정보를 조회함
    public JobDto getJobInfo(Long id){




        Job job = jobRepository.findById(id).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_JOB));

        //필요한 것만 dto에 담아서 전달
        return JobDto.builder().jobId(job.getId())
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
        errorService.isExistJob(id);
        //삭제 가능한 직업인가
        errorService.unavailableJobDelete(id);

        //삭제
        jobRepository.deleteById(id);

    }


   //신규직업등록
    public URI createJob(JobCreateRequestDto jobCreateRequestDto){

        //존재하는 학급인지
        errorService.isExistClass(jobCreateRequestDto.getClassId());

        //등록된 직업이 50개 이상이면 등록불가
        errorService.unavailableJobRegister(jobCreateRequestDto.getClassId());


        //등록가능한 직업명인지 확인
        errorService.validateJobNameInClass(jobCreateRequestDto.getClassId(), jobCreateRequestDto.getJobTitle());


        //직업등록
        Job newJob = jobCreateRequestDto.toJob(jobCreateRequestDto.getClassId(), jobCreateRequestDto.getJobTitle(), jobCreateRequestDto.getDetail(), jobCreateRequestDto.getMonthlySalary(), jobCreateRequestDto.isWithdrawStudent(), jobCreateRequestDto.isWithdrawClass()); //save에서 에러난다


        Job j = jobRepository.save(newJob);


        return createUri(j.getId(), UriTypes.JOB); //등록된 직업에 대해 URI를 같이 반환함

    }


    //새로 생성되거나 수정된 job의 id를 포함한 URI만들기
    public URI createUri(Long id, UriTypes uriTypes){
        URI uri = UriComponentsBuilder.newInstance()
//                .scheme("https")
//                .host("m-crew.iptime.org")
//                .port(8001)
                .scheme("http")
                .host("localhost")
                .port(8080)
                .path("/api/"+ uriTypes.getTypeName() + "/" + id)
                .build()
                .toUri(); //UriComponents into URI

        return uri;

    }


    public void updateAllStudentJob(List<StudentJobUpdateRequestDto> studentJobList){
        for (StudentJobUpdateRequestDto studentJob : studentJobList) {
            updateStudentJob(studentJob);
        }
    }


    public URI updateStudentJob(StudentJobUpdateRequestDto studentJob){


        //존재하는 직업인가
        errorService.isExistJob(studentJob.getJobId());


        Student student = studentRepository.findById(studentJob.getStudentId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));

        //객체의 직업을 변경
        Student updateStudent = student.updateJob(studentJob.getJobId());

        studentRepository.save(updateStudent);

        return createUri(updateStudent.getId(), UriTypes.STUDENT);

    }





}
