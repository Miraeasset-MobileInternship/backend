package miraeassetmobile.backend.service;


import miraeassetmobile.backend.domain.dto.students.StudentAccountResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentJobResponseDto;
import miraeassetmobile.backend.domain.dto.students.StudentTransferSelectorResponseDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.Job;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.JobRepository;
import miraeassetmobile.backend.repository.StudentRepository;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    StudentRepository studentRepository;
    ClassRepository classRepository;
    JobRepository jobRepository;


    public StudentService(StudentRepository studentRepository, JobRepository jobRepository,ClassRepository classRepository){
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.classRepository = classRepository;
    }


    public StudentAccountResponseDto getStudentAccountInfo(Long id){

        Student student = studentRepository.findById(id).get();

        Classes studentClass = classRepository.findById(student.getClassId()).get();


        StudentAccountResponseDto accountInfo = StudentAccountResponseDto.builder()
                .studentId(student.getId())
                .money(student.getMoney())
                .creditScore(student.getCreditScore())
                .currency(studentClass.getCurrency())
                .build();

        return accountInfo;
    }


    public StudentJobResponseDto getStudentJobInfo(Long studentId) {

        Student student = studentRepository.findById(studentId).get();

        Job studentJob = jobRepository.findById(student.getJobId()).get();

        Classes studentClass = classRepository.findById(student.getClassId()).get();

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
