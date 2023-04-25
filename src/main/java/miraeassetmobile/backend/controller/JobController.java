package miraeassetmobile.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.CreatedUriDto;
import miraeassetmobile.backend.domain.dto.classes.ClassValidInvitationResponseDto;
import miraeassetmobile.backend.domain.dto.jobs.JobCreateRequestDto;
import miraeassetmobile.backend.domain.dto.jobs.JobDto;
import miraeassetmobile.backend.domain.dto.jobs.StudentJobUpdateRequestDto;
import miraeassetmobile.backend.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


//pagenation을 위한 import


@RequestMapping("/api/job")
@RestController
public class JobController {

    private JobService jobService;


    public JobController(JobService jobService){
        this.jobService=jobService;
    }




    //job id를 통한 직업조회
    //특정 직업의 정보를 넘겨줌 (직업 수정 화면 등등)
    @GetMapping("/{job_id}")
//    @Operation(description = "해당 직업의 상세 정보를 조회, 선생님 - 직업 수정 화면")
    @Operation(summary = "직업 상세정보 조회", description = "해당 직업의 상세 정보를 조회, 선생님 - 직업 수정 화면",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success", content = @Content(schema = @Schema(implementation = JobDto.class))),
                    @ApiResponse(responseCode = "E405", description = "존재하지 않는 직업", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> getJobInfo(@PathVariable(value = "job_id") Long jobId){
        return ResponseEntity.ok(jobService.getJobInfo(jobId));
    }



    //특정 직업 삭제
    @DeleteMapping("/{job_id}/delete")
//    @Operation(description = "해당 id의 직업을 삭제, 선생님 - 직업 삭제 기능")
    @Operation(summary = "직업 삭제", description = "해당 id의 직업을 삭제, 선생님 - 직업 삭제 기능",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success - status:deleted", content = @Content(schema = @Schema(implementation = CreatedUriDto.class))),
                    @ApiResponse(responseCode = "E405", description = "존재하지 않는 직업", content = @Content ),
                    @ApiResponse(responseCode = "E701", description = "삭제 불가능한 필수 직업", content = @Content ),
                    @ApiResponse(responseCode = "E820", description = "삭제과정에서 DB에서 발생한 에러", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> deleteJob(@PathVariable(value = "job_id") Long jobId){
        return ResponseEntity.ok(jobService.deleteJob(jobId));
    }


    @PostMapping("/create")
//    @Operation(description = "새로운 직업을 생성, 선생님 - 직업 생성 화면")
    @Operation(summary = "직업 생성", description = "새로운 직업을 생성, 선생님 - 직업 생성 화면",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success - status:created", content = @Content(schema = @Schema(implementation = CreatedUriDto.class))),
                    @ApiResponse(responseCode = "E404", description = "존재하지 않는 학급", content = @Content ),
                    @ApiResponse(responseCode = "E602", description = "해당 학급에 이미 존재하는 직업명", content = @Content ),
                    @ApiResponse(responseCode = "E603", description = "학급에 직업 50개 이상 등록 불가 에러", content = @Content ),
                    @ApiResponse(responseCode = "E804", description = "생성 과정에서 DB에서 발생한 에러", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> createJob(@RequestBody @Valid JobCreateRequestDto jobInfo){

        /*
        @Valid : 유효한 JobCreateDto객체인지 검사해준다.
        httpstatus 201 CREATED URI를 같이 RETURN한다
         */
        return ResponseEntity.ok(jobService.createJob(jobInfo)); //id를 같이 반환하기


    }


    @PutMapping("/update/all")
//    @Operation(description = "아이들의 현 직업을 변경(단체), 선생님 - 아이들 직업 부여/수정(전체)")
    @Operation(summary = "학급 전체 직업 변경", description = "아이들의 현 직업을 변경(단체), 선생님 - 아이들 직업 부여/수정(전체)",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success - status:update", content = @Content(schema = @Schema(implementation = CreatedUriDto.class))),
                    @ApiResponse(responseCode = "E830", description = "정보 업데이트 과정에서 DB에서 발생한 에러", content = @Content ),
                    @ApiResponse(responseCode = "E405", description = "존재하지 않는 직업", content = @Content ),
                    @ApiResponse(responseCode = "E402", description = "존재하지 않는 학생", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> updateJobStudentAll(@RequestBody @Valid List<StudentJobUpdateRequestDto> jobList){
        return ResponseEntity.ok(jobService.updateAllStudentJob(jobList));
    }


    @PutMapping("/update")
//    @Operation(description = "아이들의 현 직업을 변경(개인), 선생님 - 아이들 직업 수정/부여(개인)")
    @Operation(summary = "학생 직업 변경", description = "아이들의 현 직업을 변경(개인), 선생님 - 아이들 직업 수정/부여(개인)",
            responses = {
                    @ApiResponse(responseCode = "E000", description = "Success - status:update", content = @Content(schema = @Schema(implementation = CreatedUriDto.class))),
                    @ApiResponse(responseCode = "E405", description = "존재하지 않는 직업", content = @Content ),
                    @ApiResponse(responseCode = "E402", description = "존재하지 않는 학생", content = @Content ),
                    @ApiResponse(responseCode = "E830", description = "정보 업데이트 과정에서 DB에서 발생한 에러", content = @Content ),
            })
    public ResponseEntity<BanklassResponseEntity> updateJobStudent(@RequestBody @Valid StudentJobUpdateRequestDto studentJobUpdateRequestDto){
        return ResponseEntity.ok(jobService.updateStudentJob(studentJobUpdateRequestDto)); //id를 같이 반환하기
    }

}
