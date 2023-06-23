package kh.com.kshrd.docengine.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String title;
    @JsonFormat(pattern = "MMM dd,h:mm a")
    private LocalDateTime editedDate;
    private Boolean status;
    private String editedBy;
    private UUID documentId;
    private UUID pageId;
    private UUID workspaceId;
}
