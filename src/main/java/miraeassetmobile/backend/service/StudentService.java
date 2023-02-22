package miraeassetmobile.backend.service;


import miraeassetmobile.backend.domain.dto.students.StudentJobDto;
import miraeassetmobile.backend.domain.entity.Job;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.repository.JobRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    StudentRepository studentRepository;
    JobRepository jobRepository;

    public StudentService(StudentRepository studentRepository, JobRepository jobRepository){
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
    }




}
