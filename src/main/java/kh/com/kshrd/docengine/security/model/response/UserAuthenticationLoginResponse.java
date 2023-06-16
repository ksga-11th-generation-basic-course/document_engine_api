package kh.com.kshrd.docengine.security.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationLoginResponse {
    private UUID userId;
    private String userName;
    private String email;
    private String token;
    private String profileImage;
    private boolean isEnable;


}
