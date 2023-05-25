package kh.com.kshrd.docengine.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse {
    private UUID userId;
    private String userName;
    private String email;
    private String profileImage;
    private Boolean isEnable;
}
