package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.enums.EAccessibility;
import kh.com.kshrd.docengine.enums.ESortCurrentDateTime;
import kh.com.kshrd.docengine.model.entity.Document;
import kh.com.kshrd.docengine.model.request.DocumentRequest;
import kh.com.kshrd.docengine.model.response.DocumentResponse;
import kh.com.kshrd.docengine.model.response.MemberResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DocumentService {
    Document createDocument(DocumentRequest documentRequest);

    Document editDocument(UUID documentId, String title);

    void currentEditing(UUID documentId);

    void setAccessibility(UUID documentId, UUID userId, UUID workspaceId, EAccessibility accessibility);

    Document viewDocument(UUID documentId);

    List<DocumentResponse> getDocumentInEachWorkspace(UUID workspaceId, Integer pageNo, Integer pageSize, ESortCurrentDateTime eSortCurrentDateTime);

    Document duplicateDocument(UUID documentId);

    List<Document> searchDocumentByTagName(UUID workspaceId, String tagName);

    void deleteDocument(UUID documentId);

    Document getDocumentByDocumentId(UUID documentId);

    Set<Document> searchDocumentByManyTagName(UUID workspaceId, List<String> tagName);

    List<MemberResponse> getAllMemberInEachDocument(UUID documentId);

    LocalDateTime getEditDate(UUID documentId);

    String recently(LocalDateTime editData);

    String getUserByDocumentId(UUID documentId);

    String getWorkspaceNameByDocumentId(UUID documentId);

    MemberResponse getUserByDocument(UUID userId);

    List<Document> getDocumentRecently();
}