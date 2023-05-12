package kh.com.kshrd.docengine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Block {
    private UUID blockId;
    private String blockType;
    private Map<String, Object> content;
    private Integer order;
    private Document documentId;
}
