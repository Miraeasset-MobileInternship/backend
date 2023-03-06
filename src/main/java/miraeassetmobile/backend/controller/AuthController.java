package miraeassetmobile.backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import miraeassetmobile.backend.domain.entity.Job;
import miraeassetmobile.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@RequestMapping("/api/auth")
@RestController
public class AuthController {

    AuthService authService;

    AuthController(AuthService authService){
        this.authService=authService;
    }

    @GetMapping("/sign-with-kakao")
    @Operation(description = "카카오로 시작하기")
    public ResponseEntity signWithKakao(@RequestParam String callBackUrl) throws URISyntaxException {
        return authService.redirectToKakaoLoginPage(callBackUrl);
    }


    @GetMapping("/get-token")
    @Operation(description = "토큰으로 교환")
    public String signWithKakao(@RequestParam(required = false) String code,
                                        @RequestParam(required = false) String state,
                                        @RequestParam(required = false) String error,
                                        @RequestParam(required = false, value = "error_description") String errorDescription
                                        ){
        return authService.getKakaoTokenWithCode(code);
    }


}
