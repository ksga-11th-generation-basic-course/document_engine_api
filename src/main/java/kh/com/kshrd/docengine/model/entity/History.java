package kh.com.kshrd.docengine.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class History {
    private UUID historyId;
    private LocalDateTime editedDate;
    private Boolean status;
    private String editedBy;
    private UUID documentId;
    private UUID pageId;
    private UUID workspaceId;
}
