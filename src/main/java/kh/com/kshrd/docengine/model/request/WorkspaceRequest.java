package kh.com.kshrd.docengine.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceRequest {
    private String workspaceName;
    private String workspaceImage;
}
