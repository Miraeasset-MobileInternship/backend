package miraeassetmobile.backend.service;


import miraeassetmobile.backend.domain.dto.students.StudentAccountResponseDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.JobRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import org.springframework.stereotype.Service;

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



}
