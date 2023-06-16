package kh.com.kshrd.docengine.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tag {
    private UUID tagId;
    private String tagName;
    private UUID workspaceId;
}
