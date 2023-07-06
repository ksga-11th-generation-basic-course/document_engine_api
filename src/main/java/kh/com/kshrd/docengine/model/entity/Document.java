package kh.com.kshrd.docengine.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "MMMM dd, yyyy h:mm a")
    private LocalDateTime createdDate;
    private List<Document> pages;
    private UUID pageId;
    private UUID workspaceId;
    private List<Tag> tags;
}