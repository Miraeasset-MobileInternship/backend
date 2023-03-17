package miraeassetmobile.backend.domain;

import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.error.StatusResponse;
import miraeassetmobile.backend.error.exception.ErrorCode;

@Getter
@Builder
public class BanklassResponseEntity {


    StatusResponse status;

    Object result;



}
