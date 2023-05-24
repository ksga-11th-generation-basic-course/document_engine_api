package kh.com.kshrd.docengine.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private UUID userId;
    private String userName;
    private String email;
    private String password;
    private String profileImage;
    private Boolean isEnable;
}
