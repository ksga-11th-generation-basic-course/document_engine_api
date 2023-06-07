package kh.com.kshrd.docengine.security.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationResetPasswordRequest {

    @NotBlank(message = "Your password must be not empty")
    @NotNull(message = "Your password may not be null")
    @Size(min = 4, message = "Your password must be have 4 character up ")
    private String newPassword;

    @NotBlank(message = "Your password must be not empty")
    @NotNull(message = "Your password may not be null")
    @Size(min = 4, message = "Your password must be have 4 character up ")
    private String newConfirmPassword;

}
