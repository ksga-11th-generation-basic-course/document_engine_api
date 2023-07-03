package kh.com.kshrd.docengine.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentAccessibilityResponse<T> {
    private UUID id;
    private UUID userId;
    private UUID documentId;
    private Boolean isOwner;
    private T accessibility;
}
