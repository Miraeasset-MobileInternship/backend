package miraeassetmobile.backend.service;


import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.CreatedUriDto;
import miraeassetmobile.backend.domain.dto.jobs.ClassJobListResponseDto;
import miraeassetmobile.backend.domain.dto.jobs.JobCreateRequestDto;
import miraeassetmobile.backend.domain.dto.jobs.JobDto;
import miraeassetmobile.backend.domain.dto.jobs.StudentJobUpdateRequestDto;
import miraeassetmobile.backend.domain.dto.students.StudentJobDto;
import miraeassetmobile.backend.domain.entity.Job;
import miraeassetmobile.backend.domain.entity.ProfileImg;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.UserInfo;
import miraeassetmobile.backend.domain.enums.UriTypes;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.ServiceException;
import miraeassetmobile.backend.repository.JobRepository;
import miraeassetmobile.backend.repository.ProfileImgRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.UserInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Service
public class JobService {


    private final JobRepository jobRepository;
    private final StudentRepository studentRepository;
    private final UserInfoRepository userInfoRepository;
    private final ProfileImgRepository profileImgRepository;
    private final ResponseService responseService;


    public JobService(ResponseService responseService, ProfileImgRepository profileImgRepository, UserInfoRepository userInfoRepository, JobRepository jobRepository, StudentRepository studentRepository){
        this.jobRepository = jobRepository;
        this.studentRepository = studentRepository;
        this.userInfoRepository = userInfoRepository;
        this.profileImgRepository = profileImgRepository;
        this.responseService = responseService;
    }



    /*
    공통직업(classId=1로 등록)을 포함한 직업을 page에 따라 10개씩 반환하는 함수
     */
    public BanklassResponseEntity getJobListByClass(Long classId){

        //존재하는 학급인지
        responseService.isExistClass(classId);

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



            return responseService.successHandler(ClassJobListResponseDto.builder()
                    .totalNum(jobLists.size())
                    .jobs(jobLists)
                    .build()
            );



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



            return responseService.successHandler(ClassJobListResponseDto.builder()
                    .totalNum(jobLists.size())
                    .jobs(jobLists)
                    .build()
            );


        }

    }


    //특정 학급의 아이들의 전체 직업과 정보를 넘김
    public BanklassResponseEntity getAllStudentJobList(Long classId){

        //존재하는 학급인지
        responseService.isExistClass(classId);


        List<Student> students = studentRepository.findByClassId(classId);

        List<StudentJobDto> studentJobs = new ArrayList<StudentJobDto>();

        for (Student student : students) {

            //존재하지 않는 직업 에러
            Job job = jobRepository.findById(student.getJobId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST));

            UserInfo u = userInfoRepository.findById(student.getUserId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST));
            ProfileImg p = profileImgRepository.findById(u.getProfileImgId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST));

            studentJobs.add(StudentJobDto.builder()
                    .studentId(student.getId())
                    .profileImg(p.getIconCode())
                    .number(student.getNumber())
                    .studentName(u.getUserName())
                    .jobId(student.getJobId())
                    .jobTitle(job.getTitle())
                    .build());

        }

        return responseService.successHandler(studentJobs);

    }



    //특정 job의 정보를 조회함
    public BanklassResponseEntity getJobInfo(Long id){


        Job job = jobRepository.findById(id).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST));

        //필요한 것만 dto에 담아서 전달
        return responseService.successHandler(
                JobDto.builder().jobId(job.getId())
                .title(job.getTitle())
                .monthlySalary(job.getMonthlySalary())
                .creditLimit(job.getCreditLimit())
                .detail(job.getDetail())
                .isWithdrawClass(job.isWithdrawClass())
                .isWithdrawStudent(job.isWithdrawStudent())
                .isModifyCredit(job.isModifyCredit())
                .build()
        );

    }

    @Transactional
    public BanklassResponseEntity deleteJob(Long id){

        //예외처리들
        //존재하는 직업인가
        responseService.isExistJob(id);

        //삭제 가능한 직업인가
        responseService.unavailableJobDelete(id);

        //삭제
        try {
            jobRepository.deleteById(id);

            return responseService.successHandler(
                    CreatedUriDto.builder()
                            .status("deleted")
                            .url(responseService.createUri(id, UriTypes.JOB))
                            .build()
            );

        }catch (Exception e){
            throw new ServiceException(ErrorCode.NOT_DELETED);
        }

    }


   //신규직업등록
    public BanklassResponseEntity createJob(JobCreateRequestDto jobCreateRequestDto){

        //존재하는 학급인지
        responseService.isExistClass(jobCreateRequestDto.getClassId());

        //등록된 직업이 50개 이상이면 등록불가
        responseService.unavailableJobRegister(jobCreateRequestDto.getClassId());


        //등록가능한 직업명인지 확인
        responseService.validateJobNameInClass(jobCreateRequestDto.getClassId(), jobCreateRequestDto.getJobTitle());


        //직업등록
        Job newJob = jobCreateRequestDto.toJob(jobCreateRequestDto.getClassId(), jobCreateRequestDto.getJobTitle(), jobCreateRequestDto.getDetail(), jobCreateRequestDto.getMonthlySalary(), jobCreateRequestDto.isWithdrawStudent(), jobCreateRequestDto.isWithdrawClass()); //save에서 에러난다


        try {

            Job j = jobRepository.save(newJob);


            return responseService.successHandler(
                    CreatedUriDto.builder()
                            .status("created")
                            .url(responseService.createUri(j.getId(), UriTypes.JOB))
                            .build()
            );


        }catch(Exception e){
            throw new ServiceException(ErrorCode.NOT_SAVE);
        }


    }





    public BanklassResponseEntity updateAllStudentJob(List<StudentJobUpdateRequestDto> studentJobList){
        for (StudentJobUpdateRequestDto studentJob : studentJobList) {
            updateStudentJob(studentJob);
        }

        return responseService.successHandler(
                CreatedUriDto.builder()
                        .status("update")
                        .url(responseService.createUri(0L, UriTypes.STUDENT))
                        .build()
        );

    }


    public BanklassResponseEntity updateStudentJob(StudentJobUpdateRequestDto studentJob){


        //존재하는 직업인가
        responseService.isExistJob(studentJob.getJobId());


        Student student = studentRepository.findById(studentJob.getStudentId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST));

        //객체의 직업을 변경
        Student updateStudent = student.updateJob(studentJob.getJobId());

        try {

            studentRepository.save(updateStudent);


            return responseService.successHandler(
                    CreatedUriDto.builder()
                            .status("update")
                            .url(responseService.createUri(updateStudent.getId(), UriTypes.STUDENT))
                            .build()
            );

        }catch(Exception e){
            throw new ServiceException(ErrorCode.NOT_SAVE);
        }

    }





}
