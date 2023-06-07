package kh.com.kshrd.docengine.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Your title may not be empty")
    @NotNull(message = "Your title may not be null")
    @Size(min = 4, max = 50, message = "Your title must be have around 50 character ")
    private String title;

    private Boolean status;

    private LocalDateTime createdDate;

    private UUID pageId;

    private UUID workspaceId;

    @NotBlank(message = "Your tags may not be empty")
    @NotNull(message = "Your tags may not be null")
    private List<UUID> tags;

}
