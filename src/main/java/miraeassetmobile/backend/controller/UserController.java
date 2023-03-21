package miraeassetmobile.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import miraeassetmobile.backend.domain.dto.auth.ClassOnboardInfo;
import miraeassetmobile.backend.domain.dto.students.StudentSalaryResponseDto;
import miraeassetmobile.backend.domain.dto.transactions.TransactionCategoryDto;
import miraeassetmobile.backend.domain.dto.users.UserInfoResponseDto;
import miraeassetmobile.backend.domain.entity.ProfileImg;
import miraeassetmobile.backend.service.UserService;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/user")
@RestController
public class UserController {


    UserService userService;


    UserController(UserService userService){
        this.userService=userService;
    }



    @GetMapping("/profile-img-list")
//    @Operation(description = "프로필 이미지 후보 리스트 보기")
    @Operation(summary = "프로필 이미지 리스트 조회", description = "프로필 이미지 후보 리스트 보기",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProfileImg.class)))),
            })
    public ResponseEntity<BanklassResponseEntity> getProfileImgList(){
        return ResponseEntity.ok(userService.getProfileImgList());
    }


    @GetMapping("/{user_id}/header-info")
//    @Operation(description = "userId를 이용해 이름, 프로필 사진을 가져오는 API")
    @Operation(summary = "이름,프로필 사진 조회", description = "userId를 이용해 이름, 프로필 사진을 가져오는 API",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = UserInfoResponseDto.class))),
            })
    public ResponseEntity<BanklassResponseEntity> getUserName(@PathVariable(value = "user_id") Long userId){
        return ResponseEntity.ok(userService.getUserName(userId));
    }


    @GetMapping("/{user_id}/student/join-class-list")
//    @Operation(description = "속해 있는 모든 학급 리스트를 반환 - 학생용")
    @Operation(summary = "속한 학급 조회", description = "속해 있는 모든 학급 리스트를 반환 - 학생용",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClassOnboardInfo.class)))),
            })
    public ResponseEntity<BanklassResponseEntity> getJoinedClass(@PathVariable(value = "user_id") Long userId){
        return ResponseEntity.ok(userService.getStudentJoinedClassList(userId));
    }


}
