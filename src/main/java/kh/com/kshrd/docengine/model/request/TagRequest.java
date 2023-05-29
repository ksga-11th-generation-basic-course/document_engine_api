package kh.com.kshrd.docengine.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TagRequest {

    @NotBlank(message = "Your tag name may not be empty")
    @NotNull(message = "Your tag name may not be null")
    @Size(min = 4, max = 50, message = "Your tag name must be have around 50 character ")
    private String tagName;

    private UUID workspaceId;
}
