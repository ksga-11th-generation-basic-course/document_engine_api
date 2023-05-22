package kh.com.kshrd.docengine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Workspace {
    private UUID workspaceId;
    private String workspaceName;
    private String workspaceCode;
    private String workspaceImage;
    private LocalDateTime createdDate;
}
