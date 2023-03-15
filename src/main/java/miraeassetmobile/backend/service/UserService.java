package miraeassetmobile.backend.service;

import miraeassetmobile.backend.domain.dto.auth.ClassOnboardInfo;
import miraeassetmobile.backend.domain.dto.users.JoinedClassResponseDto;
import miraeassetmobile.backend.domain.dto.users.ProfileImgListResponseDto;
import miraeassetmobile.backend.domain.dto.users.UserInfoResponseDto;
import miraeassetmobile.backend.domain.entity.Classes;
import miraeassetmobile.backend.domain.entity.ProfileImg;
import miraeassetmobile.backend.domain.entity.Student;
import miraeassetmobile.backend.domain.entity.UserInfo;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.NotExistException;
import miraeassetmobile.backend.repository.ClassRepository;
import miraeassetmobile.backend.repository.ProfileImgRepository;
import miraeassetmobile.backend.repository.StudentRepository;
import miraeassetmobile.backend.repository.UserInfoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    UserInfoRepository userInfoRepository;
    ProfileImgRepository profileImgRepository;
    StudentRepository studentRepository;
    ClassRepository classRepository;



    UserService(ClassRepository classRepository, StudentRepository studentRepository,UserInfoRepository userInfoRepository, ProfileImgRepository profileImgRepository){
        this.userInfoRepository = userInfoRepository;
        this.profileImgRepository = profileImgRepository;
        this.studentRepository =studentRepository;
        this.classRepository =classRepository;
    }



    public ProfileImgListResponseDto getProfileImgList(){

        return ProfileImgListResponseDto.builder()
                .profileImgList(profileImgRepository.findAll())
                .build();


    }


    public UserInfoResponseDto getUserName(Long userId){

        UserInfo user = userInfoRepository.findById(userId).orElseThrow(()-> new NotExistException(ErrorCode.NOT_EXSIT_USER));
        ProfileImg p = profileImgRepository.findById(user.getProfileImgId()).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_IMAGE));

        return UserInfoResponseDto.builder()
                .userName(user.getUserName())
                .profileImg(p.getIconCode())
                .build();
    }


    public UserInfo getUser(Long userId){

        UserInfo u = userInfoRepository.findById(userId).get();

        System.out.println(u.getUserName());

        return u;
    }


    public JoinedClassResponseDto getJoinedClassList(Long userId) {


        UserInfo u = userInfoRepository.findById(userId).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXSIT_USER));

        List<Student> studentClassList = studentRepository.findByUserId(u.getId());


        List<ClassOnboardInfo> classOnboardInfos = new ArrayList<>();

        for (Student s : studentClassList) {

            Long classId = s.getClassId(); //학생이 속한 반의 id


            //해당 반의 id로 반의 정보를 끌어오기 (1개)
            Classes c = classRepository.findById(classId).orElseThrow(() -> new NotExistException(ErrorCode.NOT_EXIST_CLASS));

            ClassOnboardInfo cInfo = ClassOnboardInfo.builder()
                    .classId(c.getId())
                    .title(c.getTitle())
                    .grade(c.getGrade() + "학년 " + c.getClassNum() + "반")
                    .teacherId(c.getTeacherId())
                    .currency(c.getCurrency())
                    .createTimestamp(c.getCreateTimestamp())
                    .build();

            classOnboardInfos.add(cInfo);


        }

        return JoinedClassResponseDto.builder()
                .classOnboardInfoList(classOnboardInfos)
                .build();

    }


}
