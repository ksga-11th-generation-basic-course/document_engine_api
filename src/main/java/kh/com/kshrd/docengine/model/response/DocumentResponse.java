package kh.com.kshrd.docengine.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kh.com.kshrd.docengine.model.entity.Block;
import kh.com.kshrd.docengine.model.entity.Document;
import kh.com.kshrd.docengine.model.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocumentResponse {
    private UUID documentId;
    private String title;
    private Boolean status;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime createdDate;
    private List<Document> pages;
    private UUID pageId;
    private UUID workspaceId;
    private List<Tag> tags;
    private String editDate;
}