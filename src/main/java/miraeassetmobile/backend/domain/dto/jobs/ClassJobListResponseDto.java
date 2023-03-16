package miraeassetmobile.backend.domain.dto.jobs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Builder
public class ClassJobListResponseDto {

//    int totalPageNum; //총 페이지의 갯수 ->pagenation 기능 요청으로 삭제

    int totalNum; //총 직업의 갯수

    List<JobDto> jobs;

}
