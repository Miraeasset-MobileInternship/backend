package miraeassetmobile.backend.controller;





import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.auth.AccessTokenInfo;
import miraeassetmobile.backend.domain.dto.auth.SignInRequestDto;
import miraeassetmobile.backend.domain.dto.auth.SignInResponseDto;
import miraeassetmobile.backend.domain.dto.auth.SignUpRequestDto;
import miraeassetmobile.backend.domain.dto.auth.sms.SendCodeRequestDto;
import miraeassetmobile.backend.service.auth.AuthService;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/api/auth")
@RestController
public class AuthController {

    AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }





    @PostMapping("/start-signin")
//    @Operation(description = "시작하기/로그인 - 가입진행된 유저일 경우(바로 로그인됨), 가입안된 유저의 경우(303 : 가입진행창으로 넘어가게)")
    @Operation(summary = "로그인, 시작하기", description = "로그인기능",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = SignInResponseDto.class))),
                    @ApiResponse(responseCode = "E101", description = "핸드폰 인증번호 맞지 않음", content = @Content ),
                    @ApiResponse(responseCode = "E306", description = "회원가입이 진행되지 않은 유저(이름 입력페이지로 넘어가 유저 정보로 가입진행되어야 함)", content = @Content ),
                    //로그인에서 발생할 수 있는 에러
                    @ApiResponse(responseCode = "E303", description = "권한이 없는 사용자", content = @Content ),
                    @ApiResponse(responseCode = "E301", description = "인증이 되지 않은 사용자", content = @Content ),
                    @ApiResponse(responseCode = "E809", description = "레디스에 토큰 저장과정에서의 에러(로그인 통합 에러)", content = @Content ),
                    @ApiResponse(responseCode = "E302", description = "올바르지 않은 핸드폰 번호(DB에 해당 번호가 없을 때)", content = @Content ),
                    //로그인 이후 반환 정보
                    @ApiResponse(responseCode = "E403", description = "존재하지 않는 유저", content = @Content ),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content ),
            })
    public ResponseEntity getStartWithSignIn(@RequestBody @Valid SignInRequestDto signInRequestDto){
        return ResponseEntity.ok(authService.getStart(signInRequestDto));
    }


    @PostMapping("/start-signup")
    @Operation(summary = "회원가입 및 자동로그인", description = "",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = SignInResponseDto.class))),
                    //회원가입 발생 에러
                    @ApiResponse(responseCode = "E305", description = "이미 해당 번호로 가입된 유저가 있음", content = @Content ),
                    @ApiResponse(responseCode = "E810", description = "회원 db저장 과정에서의 에러-회원가입 통합 에러", content = @Content ),
                    //로그인에서 발생할 수 있는 에러
                    @ApiResponse(responseCode = "E303", description = "권한이 없는 사용자", content = @Content ),
                    @ApiResponse(responseCode = "E301", description = "인증이 되지 않은 사용자", content = @Content ),
                    @ApiResponse(responseCode = "E809", description = "레디스에 토큰 저장과정에서의 에러(로그인 통합 에러)", content = @Content ),
                    @ApiResponse(responseCode = "E302", description = "올바르지 않은 핸드폰 번호(DB에 해당 번호가 없을 때)", content = @Content ),

                    //로그인 이후 반환 정보
                    @ApiResponse(responseCode = "E403", description = "존재하지 않는 유저", content = @Content ),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content ),
            })
    public ResponseEntity getStartWithSignUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto){
        return ResponseEntity.ok(authService.startWithSignUp(signUpRequestDto));

    }



    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success - no return"),
                    @ApiResponse(responseCode = "E840", description = "로그아웃 관련 redis 서버 에러 - 로그아웃 통합 에러", content = @Content ),
            })
    public ResponseEntity logout(HttpServletRequest request){
        authService.logout(request);
        return ResponseEntity.ok().build();
    }


    @PostMapping(value = "/reissue")
//    @Operation(description = "accessToken이 만료되어 401 에러를 받은 경우, 보유한 리프레스 토큰으로 갱신 요청")
    @Operation(summary = "accessToken 재발급", description = "",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = AccessTokenInfo.class))),


                    @ApiResponse(responseCode = "E006", description = "이미 로그아웃된 유저의 Token", content = @Content ),
                    @ApiResponse(responseCode = "E010", description = "유효하지 않은 refresh Token", content = @Content ),
                    @ApiResponse(responseCode = "E850", description = "재발급 과정에서의 에러(redis에서의 등록/삭제) -> 강제 로그아웃 후 재 로그인 필요", content = @Content ),




            })
    public ResponseEntity reissue(HttpServletRequest request,
                                            @RequestHeader("refreshToken") String refreshToken) {
        return ResponseEntity.ok(authService.reissue(request, refreshToken));
    }




    // coolSMS 구현 로직 연결
    // coolSMS 구현 로직 연결
    @PostMapping("/sendSMS")
    @Operation(summary = "핸드폰 문자인증번호 받기", description = "",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success - 의미 없는 body"),
                    @ApiResponse(responseCode = "E501", description = "메세지 전송 서버의 에러(외부 서버 에러)", content = @Content ),
                    @ApiResponse(responseCode = "E807", description = "인증 코드 생성과정에서 redis서버 저장시 에러", content = @Content ),
            })
    public ResponseEntity sendSMS(@RequestBody @Valid SendCodeRequestDto sendCodeRequestDto){
        return ResponseEntity.ok(authService.sendMessage(sendCodeRequestDto));
    }




}
