package miraeassetmobile.backend.service;


import miraeassetmobile.backend.domain.dto.students.StudentAccountResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentJobResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentTransferSelectorResponseDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.Job;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.UserInfo;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.JobRepository;
import miraeassetmobile.backend.repository.StudentRepository;


import miraeassetmobile.backend.repository.UserInfoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    StudentRepository studentRepository;
    ClassRepository classRepository;
    JobRepository jobRepository;
    UserInfoRepository userInfoRepository;

    ErrorService errorService;

    public StudentService(UserInfoRepository userInfoRepository, ErrorService errorService, StudentRepository studentRepository, JobRepository jobRepository, ClassRepository classRepository){
        this.errorService = errorService;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.classRepository = classRepository;
        this.userInfoRepository = userInfoRepository;
    }


    public StudentAccountResponseDto getStudentAccountInfo(Long id){

        //존재하는 학생 아닌지 검사
        Student student = studentRepository.findById(id).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));

        Classes studentClass = classRepository.findById(student.getClassId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_CLASS));


        StudentAccountResponseDto accountInfo = StudentAccountResponseDto.builder()
                .studentId(student.getId())
                .money(student.getMoney())
                .creditScore(student.getCreditScore())
                .currency(studentClass.getCurrency())
                .build();

        return accountInfo;
    }


    public StudentJobResponseDto getStudentJobInfo(Long studentId) {

        Student student = studentRepository.findById(studentId).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));

        Job studentJob = jobRepository.findById(student.getJobId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_JOB));

        Classes studentClass = classRepository.findById(student.getClassId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_CLASS));

        String classInfo = studentClass.getGrade() + "학년 " + studentClass.getClassNum() + "반";


        return StudentJobResponseDto.builder()
                .studentId(student.getId())
                .classInfo(classInfo)
                .jobId(student.getJobId())
                .JobTitle(studentJob.getTitle())
                .JobDetail(studentJob.getDetail())
                .isTransfer(studentJob.isWithdrawStudent()) // 이체하기 -> 학생계좌 출금
                .isPay(studentJob.isWithdrawClass()) //지급하기 -> 국고 출금
                .build();


    }


    public List<StudentTransferSelectorResponseDto> getStudentSelectorList(Long classId){

        //존재하는 학급인가
        errorService.isExistClass(classId);


        List<Student> studentList = studentRepository.findByClassId(classId);


        List<StudentTransferSelectorResponseDto> result = new ArrayList<>();

        for (Student s: studentList) {

            UserInfo u = userInfoRepository.findById(s.getUserId()).orElseThrow(()-> new NotExistException(ErrorCode.NOT_EXIST_STUDENT));

            result.add(StudentTransferSelectorResponseDto.builder()
                    .studentId(s.getId())
                    .studentNumber(s.getNumber())
                    .studentName(u.getUserName())
                    .studentNumberName(s.getNumber() + "번 "+u.getUserName())
                    .build());

        }

        return result;
    }

}
