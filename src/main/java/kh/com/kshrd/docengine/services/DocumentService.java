package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.Document;
import kh.com.kshrd.docengine.model.request.DocumentRequest;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    Document createDocument(DocumentRequest documentRequest);

    Document editDocument(UUID documentId, String title, List<UUID> tags);

    void currentEditing(UUID documentId);

    void setAccessibility(UUID documentId, UUID userId, String accessibility);

    List<Document> getAllDocument(Integer pageNo, Integer pageSize);

    Document viewDocument(UUID documentId);

    List<Document> getDocumentInEachWorkspace(UUID workspaceId, Integer pageNo, Integer pageSize);

    Document duplicateDocument(UUID documentId);

    Document getDocumentById(UUID documentId);

    List<Document> searchDocumentByTagName(UUID workspaceId, String tagName);

    void deleteDocument(UUID documentId);

    Document getDocumentByDocumentId(UUID documentId);
}
