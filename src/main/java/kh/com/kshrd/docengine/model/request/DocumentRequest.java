package kh.com.kshrd.docengine.model.request;

import kh.com.kshrd.docengine.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocumentRequest {
    private String title;
    private Boolean status;
    private LocalDateTime createdDate;
    private UUID pageId;
    private UUID workspaceId;
    private List<UUID> tags;
}
