package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotEditorException;
import kh.com.kshrd.docengine.exceptions.NotOwnerException;
import kh.com.kshrd.docengine.model.Block;
import kh.com.kshrd.docengine.model.Document;
import kh.com.kshrd.docengine.model.constant.Accessibility;
import kh.com.kshrd.docengine.model.request.DocumentRequest;
import kh.com.kshrd.docengine.repository.BlockRepository;
import kh.com.kshrd.docengine.repository.DocumentRepository;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import kh.com.kshrd.docengine.services.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class DocumentServiceImp implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserAuthenticationService userAuthenticationService;
    private final BlockRepository blockRepository;

    @Override
    public Document createDocument(DocumentRequest documentRequest) {
        Document document = documentRepository.createDocument(documentRequest);
        documentRepository.addDataToUserDocument(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId());
        documentRequest.getTags().forEach(tagId -> documentRepository.InsertTagIdAndDocumentIdIntoTagDocument(tagId, document.getDocumentId()));
        return document;
    }

    @Override
    public Document editDocument(UUID documentId, String title) {
        String checkAccessibility = documentRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), documentId);
        if(!Objects.equals(checkAccessibility, "Editor")){
            throw new NotEditorException("Your accessibility is not editor");
        }
        return documentRepository.editDocument(documentId, title);
    }

    @Override
    public void currentEditing(UUID documentId) {
        documentRepository.currentEditing(documentId);
    }

    @Override
    public void setAccessibility(UUID documentId, UUID userId, String accessibility) {
        Boolean isOwner = documentRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), documentId);
        if(isOwner) {
            boolean isTrue = false;
            for (Accessibility access : Accessibility.values()) {
                if (accessibility.equalsIgnoreCase(access.name())) {
                    isTrue = true;
                    break;
                }
            }
            if (!isTrue) {
                throw new BadRequestException("This accessibility is not correct : 'String' , " +
                        "please input one of (EDITOR, VIEWER and NO_ACCESS)");
            } else if (accessibility.isBlank()) {
                throw new BadRequestException("This field could not empty");
            }
            documentRepository.setAccessibility(documentId, userId, accessibility);
        }else{
            throw new NotOwnerException("You are not owner");
        }
    }

    @Override
    public List<Document> getAllDocument() {
        return documentRepository.getAllDocument();
    }

    @Override
    public Document viewDocument(UUID documentId) {
        return documentRepository.viewDocument(documentId);
    }

    @Override
    public List<Document> getDocumentInEachWorkspace(UUID workspaceId) {
        return documentRepository.getDocumentInEachWorkspace(workspaceId);
    }

    @Override
    public Document duplicateDocument(UUID documentId) {
        Document document = documentRepository.duplicateDocument(documentId);
        List<Block> blocks = blockRepository.duplicateBlock(documentId);
        for (Block block : blocks){
            blockRepository.updateDocumentIdForDuplicateBlock(document.getDocumentId(), block.getBlockId());
        }
        return document;
    }

    @Override
    public Document getDocumentById(UUID documentId) {
        return documentRepository.getDocumentById(documentId);
    }

    @Override
    public List<Document> searchDocumentByTagName(UUID workspaceId, String tagName) {
        return documentRepository.searchDocumentByTagName(workspaceId, tagName);
    }

    @Override
    public void deleteDocument(UUID documentId) {
        Boolean isOwner = documentRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), documentId);
        if(isOwner){
            documentRepository.deleteDocument(documentId);
        }else{
            throw new NotOwnerException("You are not owner");
        }
    }

}
