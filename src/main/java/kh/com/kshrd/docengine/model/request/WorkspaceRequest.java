package kh.com.kshrd.docengine.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceRequest {
    @NotBlank(message = "Your workspace name may not be empty")
    @NotNull(message = "Your workspace name may not be null")
    @Size(min = 4, max = 50, message = "Your workspace name must be have around 50 character ")
    private String workspaceName;

    private String workspaceImage;
}
