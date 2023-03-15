package miraeassetmobile.backend.domain.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOnboardInfo {

    Long userId;


    String userRole;


    String userName;

    String profileImg;


    List<ClassOnboardInfo> classInfo;

}
