package miraeassetmobile.backend.domain;


import lombok.Builder;
import lombok.Getter;
import miraeassetmobile.backend.error.StatusResponse;


@Getter
@Builder
public class BanklassResponseEntity {


    StatusResponse status;

    Object result;



}
