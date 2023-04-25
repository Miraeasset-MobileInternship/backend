package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.CreatedUriDto;
import miraeassetmobile.backend.domain.dto.students.*;
import miraeassetmobile.backend.domain.entity.*;
import miraeassetmobile.backend.domain.enums.UriTypes;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.ServiceException;
import miraeassetmobile.backend.repository.*;
import miraeassetmobile.backend.repository.redis.ClassInvitationCodeRedisRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    StudentRepository studentRepository;
    ClassRepository classRepository;
    JobRepository jobRepository;
    UserInfoRepository userInfoRepository;
    ProfileImgRepository profileImgRepository;
    ClassInvitationCodeRedisRepository classInvitationCodeRedisRepository;



    ResponseService responseService;


    public StudentService(ResponseService responseService, ClassInvitationCodeRedisRepository classInvitationCodeRedisRepository, ProfileImgRepository profileImgRepository, UserInfoRepository userInfoRepository, StudentRepository studentRepository, JobRepository jobRepository, ClassRepository classRepository){

        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.classRepository = classRepository;
        this.userInfoRepository = userInfoRepository;
        this.profileImgRepository = profileImgRepository;
        this.classInvitationCodeRedisRepository =classInvitationCodeRedisRepository;
        this.responseService=responseService;
    }


    public BanklassResponseEntity getStudentAccountInfo(Long id){

        //존재하는 학생 아닌지 검사
        Student student = studentRepository.findById(id).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));

        Classes studentClass = classRepository.findById(student.getClassId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_CLASS));

        UserInfo teacher = userInfoRepository.findById(studentClass.getTeacherId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));


        StudentAccountResponseDto result = StudentAccountResponseDto.builder()
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

        return responseService.successHandler(result);
    }


    public BanklassResponseEntity getStudentJobInfo(Long studentId) {

        Student student = studentRepository.findById(studentId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));

        Job studentJob = jobRepository.findById(student.getJobId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_JOB));

        Classes studentClass = classRepository.findById(student.getClassId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_CLASS));

        UserInfo u = userInfoRepository.findById(student.getUserId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));

        ProfileImg p = profileImgRepository.findById(u.getProfileImgId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_IMAGE));

        String classInfo = studentClass.getGrade() + "학년 " + studentClass.getClassNum() + "반";


        return responseService.successHandler(
                StudentJobResponseDto.builder()
                        .studentId(student.getId())
                        .classInfo(classInfo)
                        .jobId(student.getJobId())
                        .JobTitle(studentJob.getTitle())
                        .JobDetail(studentJob.getDetail())
                        .profileImg(p.getIconCode())
                        .isTransfer(studentJob.isWithdrawStudent()) // 이체하기 -> 학생계좌 출금
                        .isPay(studentJob.isWithdrawClass()) //지급하기 -> 국고 출금
                        .build())
                ;


    }


    public BanklassResponseEntity getStudentSelectorList(Long classId){

        //존재하는 학급인가
        responseService.isExistClass(classId);


        List<Student> studentList = studentRepository.findByClassId(classId);


        List<StudentTransferSelectorDto> result = new ArrayList<>();

        for (Student s: studentList) {

            UserInfo u = userInfoRepository.findById(s.getUserId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_USER));

            result.add(StudentTransferSelectorDto.builder()
                    .studentId(s.getId())
                    .studentNumber(s.getNumber())
                    .studentName(u.getUserName())
                    .build());

        }

        return responseService.successHandler(result);
    }

    public BanklassResponseEntity getStudentSalary(Long studentId){

        Student s = studentRepository.findById(studentId).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_STUDENT));

        Job j = jobRepository.findById(s.getJobId()).orElseThrow(() -> new ServiceException(ErrorCode.NOT_EXIST_JOB));

        return
                responseService.successHandler(
                        StudentSalaryResponseDto.builder()
                                .jobId(j.getId())
                                .jobTitle(j.getTitle())
                                .monthlySalary(j.getMonthlySalary())
                                .build()
                );

    }


    @Transactional
    public BanklassResponseEntity createStudentInClass(StudentClassJoinRequestDto studentClassJoinRequestDto){

        //해당 유저가 이미 해당 반에 존재하면 가입을 막아야함
        responseService.isUserExistInClass(studentClassJoinRequestDto.getClassId(), studentClassJoinRequestDto.getUserId());

        //해당 학급 번호가 이미 사용중이거나 입력되지 않았는가
        if(studentRepository.existsByClassIdAndNumber(studentClassJoinRequestDto.getClassId(),studentClassJoinRequestDto.getStudentNumber())){
            throw new ServiceException(ErrorCode.ALREADY_EXIST_CLASS_STUDENT_NUMBER);
        }


        //


        Student newStudent = Student.builder()
                .userId(studentClassJoinRequestDto.getUserId())
                .number(studentClassJoinRequestDto.getStudentNumber())
                .classId(studentClassJoinRequestDto.getClassId())
                .build();

        try {
            Student s = studentRepository.save(newStudent);

            return responseService.successHandler(

                    StudentClassJoinResponseDto.builder()
                            .studentId(s.getId())
                            .build()

            );

        }catch (Exception e){
            //저장하는 과정에서 에러가 발생했을 경우
            throw new ServiceException(ErrorCode.NOT_SAVE_JOIN_CLASS);
        }


    }


}
