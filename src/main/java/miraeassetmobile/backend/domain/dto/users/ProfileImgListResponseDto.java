package miraeassetmobile.backend.domain.dto.users;
import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.domain.entity.ProfileImg;

import java.util.List;


//v2에서는 사용안함
@Getter
@Builder
public class ProfileImgListResponseDto {

    List<ProfileImg> profileImgList;

}
