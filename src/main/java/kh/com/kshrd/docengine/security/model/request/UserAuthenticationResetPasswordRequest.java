package kh.com.kshrd.docengine.security.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationResetPasswordRequest {

    private String newPassword;
    private String newConfirmPassword;

}
