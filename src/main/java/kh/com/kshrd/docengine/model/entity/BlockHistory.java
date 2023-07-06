package kh.com.kshrd.docengine.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlockHistory {
    private UUID blockHistoryId;
    private String blockType;
    private Map<String, Object> content;
    private Integer order;
    private UUID historyId;
}