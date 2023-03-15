package miraeassetmobile.backend.controller;





import io.swagger.v3.oas.annotations.Operation;
import miraeassetmobile.backend.domain.dto.auth.SignInRequestDto;
import miraeassetmobile.backend.domain.dto.auth.SignUpRequestDto;
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
    @Operation(description = "시작하기/로그인 - 가입진행된 유저일 경우(바로 로그인됨), 가입안된 유저의 경우(303 : 가입진행창으로 넘어가게)")
    public ResponseEntity getStartWithSignIn(@RequestBody @Valid SignInRequestDto signInRequestDto){
        return ResponseEntity.ok(authService.getStart(signInRequestDto));
    }


    @PostMapping("/start-signup")
    @Operation(description = "회원가입 및 자동로그인")
    public ResponseEntity getStartWithSignUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto){
        return ResponseEntity.ok(authService.startWithSignUp(signUpRequestDto));

    }


    @PostMapping("/logout")
    @Operation(description = "로그아웃")
    public ResponseEntity logout(HttpServletRequest request){
        authService.logout(request);
        return ResponseEntity.ok().build();
    }


    @PostMapping(value = "/reissue")
    @Operation(description = "accessToken이 만료되어 401 에러를 받은 경우, 보유한 리프레스 토큰으로 갱신 요청")
    public ResponseEntity reissue(HttpServletRequest request,
                                            @RequestHeader String refreshToken) {
        return ResponseEntity.ok(authService.reissue(request, refreshToken));
    }





    // coolSMS 구현 로직 연결
    @PostMapping("/sendSMS")
    public ResponseEntity sendSMS(@RequestParam(value="phone_number") String phoneNumber) throws CoolsmsException {
        authService.sendMessage(phoneNumber);
        return ResponseEntity.ok().build();
    }




}
