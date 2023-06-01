package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.entity.Document;
import kh.com.kshrd.docengine.model.request.DocumentRequest;
import kh.com.kshrd.docengine.model.response.MemberResponse;
import kh.com.kshrd.docengine.model.response.UserResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DocumentService {
    Document createDocument(DocumentRequest documentRequest);

    Document editDocument(UUID documentId, String title);

    void currentEditing(UUID documentId);

    void setAccessibility(UUID documentId, UUID userId, String accessibility);

    Document viewDocument(UUID documentId);

    List<Document> getDocumentInEachWorkspace(UUID workspaceId, Integer pageNo, Integer pageSize);

    Document duplicateDocument(UUID documentId);

    List<Document> searchDocumentByTagName(UUID workspaceId, String tagName);

    void deleteDocument(UUID documentId);

    Document getDocumentByDocumentId(UUID documentId);

    Set<Document> searchDocumentByManyTagName(UUID workspaceId, List<String> tagName);

    List<MemberResponse> getAllMemberInEachDocument(UUID documentId);
}
