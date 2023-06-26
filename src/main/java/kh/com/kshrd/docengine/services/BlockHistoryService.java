package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.entity.BlockHistory;

import java.util.List;
import java.util.UUID;

public interface BlockHistoryService {
    List<BlockHistory> getBlockForEachHistory(UUID historyId);
}
