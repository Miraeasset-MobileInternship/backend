package miraeassetmobile.backend.domain.dto.users;

import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.domain.entity.ProfileImg;

import java.util.List;

@Getter
@Builder
public class ProfileImgListResponseDto {

    List<ProfileImg> profileImgList;

}
