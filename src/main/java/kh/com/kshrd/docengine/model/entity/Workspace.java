package kh.com.kshrd.docengine.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import kh.com.kshrd.docengine.model.response.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Workspace {
    private UUID workspaceId;
    private String workspaceName;
    private String workspaceCode;
    private String workspaceImage;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime createdDate;
    private Boolean isOwner;
    private List<MemberResponse> members;
    private Integer totalDocument;
}
