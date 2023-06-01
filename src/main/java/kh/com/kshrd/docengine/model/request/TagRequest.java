package kh.com.kshrd.docengine.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TagRequest {
    private String tagName;
    private UUID workspaceId;
    private UUID document_id;
}
