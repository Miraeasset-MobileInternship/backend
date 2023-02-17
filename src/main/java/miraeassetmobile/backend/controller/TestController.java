package miraeassetmobile.backend.controller;

import lombok.RequiredArgsConstructor;
import miraeassetmobile.backend.domain.TestInfo;
import miraeassetmobile.backend.service.TestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api")
@RestController
public class TestController {


    private final TestService testService;

    public TestController(TestService testService){
        this.testService = testService;
    }


    @GetMapping("/test")
    public TestInfo getTestInfo(@RequestParam(required = false, value = "id") Integer id){
        return testService.getTestInfo(id);
    }




}
