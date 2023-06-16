package kh.com.kshrd.docengine.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse<T> {
    private UUID userId;
    private String username;
    private String email;
    private Boolean isOwner;
    private String profileImage;
    private T accessibility;
}
