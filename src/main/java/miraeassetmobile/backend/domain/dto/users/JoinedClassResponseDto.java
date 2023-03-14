package miraeassetmobile.backend.domain.dto.users;

import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.domain.dto.auth.ClassOnboardInfo;

import java.util.List;

@Getter
@Builder
public class JoinedClassResponseDto {


    List<ClassOnboardInfo> classOnboardInfoList;

}
