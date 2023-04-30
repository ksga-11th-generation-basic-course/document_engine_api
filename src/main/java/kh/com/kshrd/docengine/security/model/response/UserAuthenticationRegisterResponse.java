package kh.com.kshrd.docengine.security.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthenticationRegisterResponse {
    private String username;
    private String email;
    private String profileImage;
    private boolean isEnable;
}
