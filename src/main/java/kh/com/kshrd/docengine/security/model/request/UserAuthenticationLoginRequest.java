package kh.com.kshrd.docengine.security.model.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationLoginRequest {

    /*  email validate*/
    @NotBlank(message = "Your email may not be empty")
    @NotNull(message = "Your email may not be null")
    @Size(min = 8, max = 50, message = "Your email must be have around 50 character ")
    @Email(message = "Your email invalid")
    private String email;

    /*    password validate*/
    @NotBlank(message = "Your password must be not empty")
    @NotNull(message = "Your password may not be null")
    @Size(min = 4, message = "Your email must be have around 8 character ")
    private String password;

}
