package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.model.entity.History;
import kh.com.kshrd.docengine.repository.BlockRepository;
import kh.com.kshrd.docengine.repository.HistoryRepository;
import kh.com.kshrd.docengine.services.HistoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class HistoryServiceImp implements HistoryService {

    private final HistoryRepository historyRepository;
    private final BlockRepository blockRepository;

    @Override
    public List<History> getHistoryInEachDocument(UUID documentId) {
        if (documentId == null) {
            throw new BadRequestException("Document id cannot be null");
        } else if (documentId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank and empty");
        }
        return historyRepository.getHistoryInEachDocument(documentId);
    }

    @Override
    public void restoreDocument(UUID historyId, UUID documentId) {
        if (historyId == null) {
            throw new BadRequestException("History id cannot be null");
        } else if (documentId == null) {
            throw new BadRequestException("Document id cannot be null");
        } else if (historyId.toString().isBlank()) {
            throw new BadRequestException("History id cannot be blank and empty");
        } else if (documentId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank and empty");
        }
        blockRepository.deleteBlockByDocumentId(documentId);
        blockRepository.restoreBlockDocument(historyId);
        History history = historyRepository.getHistoryByHistoryId(historyId);
        if(history == null){
            throw new NotFoundException("History doesn't exist");
        }
        historyRepository.restoreDocument(history.getTitle(), documentId);
    }

    @Override
    public History getHistoryByHistoryId(UUID historyId) {
        if (historyId == null) {
            throw new BadRequestException("History id cannot be null");
        } else if (historyId.toString().isBlank()) {
            throw new BadRequestException("History id cannot be blank and empty");
        }
        return historyRepository.getHistoryByHistoryId(historyId);
    }
}
