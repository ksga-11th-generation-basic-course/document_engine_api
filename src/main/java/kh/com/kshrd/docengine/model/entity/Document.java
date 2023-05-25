package kh.com.kshrd.docengine.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Document {
    private UUID documentId;
    private String title;
    private Boolean status;
    private LocalDateTime createdDate;
    private UUID pageId;
    private UUID workspaceId;
    private List<Tag> tags;
}
