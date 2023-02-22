package miraeassetmobile.backend.service;


import miraeassetmobile.backend.repository.JobRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    StudentRepository studentRepository;
    JobRepository jobRepository;

    public StudentService(StudentRepository studentRepository, JobRepository jobRepository){
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
    }




}
