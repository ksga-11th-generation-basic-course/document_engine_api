package kh.com.kshrd.docengine.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response<T> {

    private HttpStatus status;

    private T payload;
    private LocalDateTime dateTime;

    private String message;

}
