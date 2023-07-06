package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.model.entity.BlockHistory;
import kh.com.kshrd.docengine.repository.BlockHistoryRepository;
import kh.com.kshrd.docengine.services.BlockHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BlockHistoryServiceImp implements BlockHistoryService {

    final private BlockHistoryRepository blockHistoryRepository;

    @Override
    public List<BlockHistory> getBlockForEachHistory(UUID historyId) {
        if (historyId == null) {
            throw new BadRequestException("Document id cannot be null");
        } else if (historyId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank or empty");
        }
        return blockHistoryRepository.getBlockForEachHistory(historyId);
    }
}