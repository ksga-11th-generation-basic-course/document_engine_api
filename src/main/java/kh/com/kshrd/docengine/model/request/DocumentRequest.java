package kh.com.kshrd.docengine.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocumentRequest {
    private String title;
    private Boolean status;
    private UUID pageId;
    private UUID workspaceId;
}