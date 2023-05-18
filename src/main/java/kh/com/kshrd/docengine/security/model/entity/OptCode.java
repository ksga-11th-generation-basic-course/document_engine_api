package kh.com.kshrd.docengine.security.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptCode {

    private UUID optId;
    private String digitCode;
    private LocalDateTime createdDate;
    private LocalDateTime expiredDate;
    private Boolean hasVerified;
    private UUID userId;

}
