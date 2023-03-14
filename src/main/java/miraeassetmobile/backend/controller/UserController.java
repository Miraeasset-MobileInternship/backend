package miraeassetmobile.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import miraeassetmobile.backend.domain.dto.students.StudentClassJoinRequestDto;
import miraeassetmobile.backend.domain.dto.transactions.TransactionDetailResponseDto;
import miraeassetmobile.backend.domain.dto.users.JoinedClassResponseDto;
import miraeassetmobile.backend.domain.dto.users.ProfileImgListResponseDto;
import miraeassetmobile.backend.domain.dto.users.UserInfoResponseDto;
import miraeassetmobile.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api/user")
@RestController
public class UserController {


    UserService userService;


    UserController(UserService userService){
        this.userService=userService;
    }



    @GetMapping("/profile-img-list")
    @Operation(description = "프로필 이미지 후보 리스트 보기")
    public ResponseEntity<ProfileImgListResponseDto> getProfileImgList(){
        return ResponseEntity.ok(userService.getProfileImgList());
    }


    @GetMapping("/{user_id}/header-info")
    @Operation(description = "userId를 이용해 이름, 프로필 사진을 가져오는 API")
    public ResponseEntity<UserInfoResponseDto> getUserName(@PathVariable(value = "user_id") Long userId){
        return ResponseEntity.ok(userService.getUserName(userId));
    }



    @GetMapping("/{user_id}/student/join-class-list")
    @Operation(description = "속해 있는 모든 학급 리스트를 반환 - 학생용")
    public ResponseEntity<JoinedClassResponseDto> getJoinedClass(@PathVariable(value = "user_id") Long userId){
        return ResponseEntity.ok(userService.getJoinedClassList(userId));
    }


}
