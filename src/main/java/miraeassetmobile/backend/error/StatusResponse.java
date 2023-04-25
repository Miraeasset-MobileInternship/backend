package miraeassetmobile.backend.error;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StatusResponse {
    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
//    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String status;
    private final String message;

}
