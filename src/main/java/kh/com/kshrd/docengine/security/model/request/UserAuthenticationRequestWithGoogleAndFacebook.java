package kh.com.kshrd.docengine.security.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAuthenticationRequestWithGoogleAndFacebook {
    private String username;
    private String email;
    private String password;
    private String profileImage;
}
