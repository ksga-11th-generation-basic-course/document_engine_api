package kh.com.kshrd.docengine.security.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationLoginResponse {

    private String userName;
    private String email;
    private String token;
    private String profileImage;
    private boolean isEnable;


}
