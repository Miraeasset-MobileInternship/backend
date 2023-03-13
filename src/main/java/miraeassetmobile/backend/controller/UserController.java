package miraeassetmobile.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import miraeassetmobile.backend.domain.dto.transactions.TransactionDetailResponseDto;
import miraeassetmobile.backend.domain.dto.users.ProfileImgListResponseDto;
import miraeassetmobile.backend.domain.dto.users.UserInfoResponseDto;
import miraeassetmobile.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}
