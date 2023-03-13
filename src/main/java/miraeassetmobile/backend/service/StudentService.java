package miraeassetmobile.backend.service;


import miraeassetmobile.backend.domain.dto.students.*;
import miraeassetmobile.backend.domain.entity.*;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.repository.*;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    StudentRepository studentRepository;
    ClassRepository classRepository;
    JobRepository jobRepository;
    UserInfoRepository userInfoRepository;
    ProfileImgRepository profileImgRepository;

    ErrorService errorService;

    public StudentService(ProfileImgRepository profileImgRepository,UserInfoRepository userInfoRepository, ErrorService errorService, StudentRepository studentRepository, JobRepository jobRepository, ClassRepository classRepository){
        this.errorService = errorService;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.classRepository = classRepository;
        this.userInfoRepository = userInfoRepository;
        this.profileImgRepository = profileImgRepository;
    }


    public StudentAccountResponseDto getStudentAccountInfo(Long id){

        //존재하는 학생 아닌지 검사
        Student student = studentRepository.findById(id).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));

        Classes studentClass = classRepository.findById(student.getClassId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_CLASS));

        UserInfo teacher = userInfoRepository.findById(studentClass.getTeacherId()).orElseThrow(()->new NotExistException(ErrorCode.NOT_EXSIT_USER));


        StudentAccountResponseDto accountInfo = StudentAccountResponseDto.builder()
                .studentId(student.getId())
                .money(student.getMoney())
                .creditScore(student.getCreditScore())
                .currency(studentClass.getCurrency())
                .classGrade(studentClass.getGrade())
                .classNumber(studentClass.getClassNum())
                .schoolName(studentClass.getSchoolName())
                .studentNumber(student.getNumber())
                .teacherName(teacher.getUserName())
                .build();

        return accountInfo;
    }


    public StudentJobResponseDto getStudentJobInfo(Long studentId) {

        Student student = studentRepository.findById(studentId).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));

        Job studentJob = jobRepository.findById(student.getJobId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_JOB));

        Classes studentClass = classRepository.findById(student.getClassId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_CLASS));

        UserInfo u = userInfoRepository.findById(student.getUserId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXSIT_USER));

        ProfileImg p = profileImgRepository.findById(u.getProfileImgId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_IMAGE));

        String classInfo = studentClass.getGrade() + "학년 " + studentClass.getClassNum() + "반";


        return StudentJobResponseDto.builder()
                .studentId(student.getId())
                .classInfo(classInfo)
                .jobId(student.getJobId())
                .JobTitle(studentJob.getTitle())
                .JobDetail(studentJob.getDetail())
                .profileImg(p.getIconCode())
                .isTransfer(studentJob.isWithdrawStudent()) // 이체하기 -> 학생계좌 출금
                .isPay(studentJob.isWithdrawClass()) //지급하기 -> 국고 출금
                .build();


    }


    public StudentSelectorListResponseDto getStudentSelectorList(Long classId){

        //존재하는 학급인가
        errorService.isExistClass(classId);


        List<Student> studentList = studentRepository.findByClassId(classId);


        List<StudentTransferSelectorDto> result = new ArrayList<>();

        for (Student s: studentList) {

            UserInfo u = userInfoRepository.findById(s.getUserId()).orElseThrow(()-> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));

            result.add(StudentTransferSelectorDto.builder()
                    .studentId(s.getId())
                    .studentNumber(s.getNumber())
                    .studentName(u.getUserName())
                    .studentNumberName(s.getNumber() + "번 "+u.getUserName())
                    .build());

        }

        return StudentSelectorListResponseDto.builder()
                .studentSelectorList(result)
                .build();
    }

    public StudentSalaryResponseDto getStudentSalary(Long studentId){

        Student s = studentRepository.findById(studentId).orElseThrow(()-> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));

        Job j = jobRepository.findById(s.getJobId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_JOB));

        return StudentSalaryResponseDto.builder()
                .jobId(j.getId())
                .jobTitle(j.getTitle())
                .monthlySalary(j.getMonthlySalary())
                .build();

    }

}
