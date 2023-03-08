package miraeassetmobile.backend.controller;





import io.swagger.v3.oas.annotations.Operation;
import miraeassetmobile.backend.domain.dto.auth.SignInRequestDto;
import miraeassetmobile.backend.domain.dto.auth.SignUpRequestDto;
import miraeassetmobile.backend.service.auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api/auth")
@RestController
public class AuthController {

    AuthService authService;

    AuthController(AuthService authService){
        this.authService=authService;
    }



    @PostMapping("/start")
    @Operation(description = "시작하기 ")
    public ResponseEntity getStartWithSignIn(@RequestBody @Valid SignInRequestDto signInRequestDto){
        System.out.println(signInRequestDto.getPhoneNumber());
        return ResponseEntity.ok(authService.getStart(signInRequestDto));
    }


    @PostMapping("/signup")
    @Operation(description = "회원 가입부터 시작하기 ")
    public ResponseEntity getStartWithSignUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto){
        return ResponseEntity.ok(authService.startWithSignUp(signUpRequestDto));

    }


}
