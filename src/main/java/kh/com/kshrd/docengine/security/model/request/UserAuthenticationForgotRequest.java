package kh.com.kshrd.docengine.security.model.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationForgotRequest {

    @NotBlank(message = "Your email may not be empty")
    @NotNull(message = "Your email may not be null")
    @Size(min = 8, max = 50, message = "Your email must be have around 50 character ")
    @Email(message = "Your email invalid")
    private String email;


}
