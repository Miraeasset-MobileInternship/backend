package miraeassetmobile.backend.domain.dto.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class UserClassInfo {

    int totalData;


    List<ClassOnboardInfo> classInfo;

}
