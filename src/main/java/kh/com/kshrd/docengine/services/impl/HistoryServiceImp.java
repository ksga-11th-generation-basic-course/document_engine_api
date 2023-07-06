package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotEditorException;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.exceptions.NotOwnerException;
import kh.com.kshrd.docengine.model.entity.Document;
import kh.com.kshrd.docengine.model.entity.History;
import kh.com.kshrd.docengine.repository.BlockRepository;
import kh.com.kshrd.docengine.repository.DocumentRepository;
import kh.com.kshrd.docengine.repository.HistoryRepository;
import kh.com.kshrd.docengine.repository.TagRepository;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import kh.com.kshrd.docengine.services.HistoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class HistoryServiceImp implements HistoryService {

    private final HistoryRepository historyRepository;
    private final BlockRepository blockRepository;
    private final DocumentRepository documentRepository;
    private final UserAuthenticationService userAuthenticationService;
    private final TagRepository tagRepository;

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
    public String restoreDocument(UUID historyId, UUID documentId) {
        validateDocumentIdAndHistoryId(historyId, documentId);

        Document document = documentRepository.getDocumentByDocumentId(documentId);

        if (document == null) {
            throw new NotFoundException("Document doesn't exist");
        } else {

            String checkAccessibility = documentRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId());

            if (Objects.equals(checkAccessibility, "VIEWER") || Objects.equals(checkAccessibility, "NO_ACCESS")) {
                throw new NotEditorException("Your accessibility cannot restore this document");
            } else {
                blockRepository.deleteBlockByDocumentId(documentId);
                blockRepository.restoreBlockDocument(historyId);
                tagRepository.deleteTagByDocumentId(documentId);
                tagRepository.restoreTagDocument(historyId);
                History history = historyRepository.getHistoryByHistoryId(historyId);
                if (history == null) {
                    throw new NotFoundException("History doesn't exist");
                }
                return historyRepository.restoreDocument(history.getTitle(), documentId);
            }
        }
    }

    @Override
    public History getHistoryByHistoryId(UUID historyId) {
        if (historyId == null) {
            throw new BadRequestException("History id cannot be null");
        } else if (historyId.toString().isBlank()) {
            throw new BadRequestException("History id cannot be blank and empty");
        }
        History history = historyRepository.getHistoryByHistoryId(historyId);
        if (history == null) {
            throw new NotFoundException("History doesn't exist");
        }
        return historyRepository.getHistoryByHistoryId(historyId);
    }

    @Override
    public void removeHistory(UUID historyId, UUID documentId) {
        validateDocumentIdAndHistoryId(historyId, documentId);

        Document document = documentRepository.getDocumentByDocumentId(documentId);

        if (document == null) {
            throw new NotFoundException("Document doesn't exist");
        } else {
            Boolean isDocumentOwner = documentRepository.isDocumentOwner(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId());
            if (isDocumentOwner) {
                History history = historyRepository.getHistoryByHistoryId(historyId);
                if (history == null) {
                    throw new NotFoundException("History doesn't exist");
                }
                historyRepository.removeHistory(historyId, documentId);
            } else {
                throw new NotOwnerException("You are owner, you cannot remove this history");
            }
        }
    }

    private void validateDocumentIdAndHistoryId(UUID historyId, UUID documentId) {
        if (historyId == null) {
            throw new BadRequestException("History id cannot be null");
        } else if (documentId == null) {
            throw new BadRequestException("Document id cannot be null");
        } else if (historyId.toString().isBlank()) {
            throw new BadRequestException("History id cannot be blank and empty");
        } else if (documentId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank and empty");
        }
    }
}