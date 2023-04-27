package kh.com.kshrd.docengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthDTO {

    private String userName;
    private String email;
    private String token;
    private String profileImage;
    private boolean isEnable;


}
