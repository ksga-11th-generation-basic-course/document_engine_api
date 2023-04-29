package kh.com.kshrd.docengine.security.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationRegisterRequest {

    private String username;

    private String email;

    private String password;

}
