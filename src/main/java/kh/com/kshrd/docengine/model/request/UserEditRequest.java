package kh.com.kshrd.docengine.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEditRequest {
    private String username;
    private String profileImage;
}
