package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.model.History;
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

    @Override
    public List<History> getHistoryInEachDocument(UUID documentId) {
        return historyRepository.getHistoryInEachDocument(documentId);
    }
}
