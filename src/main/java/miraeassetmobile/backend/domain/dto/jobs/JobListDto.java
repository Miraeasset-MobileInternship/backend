package miraeassetmobile.backend.domain.dto.jobs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import miraeassetmobile.backend.domain.entity.Job;

import java.util.List;


@Getter
@Setter
@Builder
public class JobListDto {

    int totalPageNum; //총 페이지의 갯수

    int totalNum; //총 직업의 갯수

    List<Job> jobs;

}
