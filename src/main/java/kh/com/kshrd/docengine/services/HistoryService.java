package kh.com.kshrd.docengine.services;


import kh.com.kshrd.docengine.model.entity.History;

import java.util.List;
import java.util.UUID;

public interface HistoryService {
    List<History> getHistoryInEachDocument(UUID documentId);
}
