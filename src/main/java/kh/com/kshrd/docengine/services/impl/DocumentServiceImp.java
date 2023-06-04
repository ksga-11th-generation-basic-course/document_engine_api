package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotEditorException;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.exceptions.NotOwnerException;
import kh.com.kshrd.docengine.model.entity.*;
import kh.com.kshrd.docengine.enums.EAccessibility;
import kh.com.kshrd.docengine.model.request.DocumentRequest;
import kh.com.kshrd.docengine.model.response.MemberResponse;
import kh.com.kshrd.docengine.repository.*;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import kh.com.kshrd.docengine.services.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Service
public class DocumentServiceImp implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserAuthenticationService userAuthenticationService;
    private final BlockRepository blockRepository;
    private final HistoryRepository historyRepository;
    private final BlockHistoryRepository blockHistoryRepository;
    private final WorkspaceRepository workspaceRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Override
    public Document createDocument(DocumentRequest documentRequest) {

        if (documentRequest.getTitle() == null) {
            throw new BadRequestException("Title cannot be null");
        } else if (documentRequest.getTitle().isBlank()) {
            throw new BadRequestException("Title cannot be blank or empty");
        }

        Boolean isCheckAccessibility = workspaceRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), documentRequest.getWorkspaceId());
        if (isCheckAccessibility) {
            Document document = documentRepository.createDocument(documentRequest);
            documentRepository.addDataToUserDocument(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId());
            List<UUID> usersId = workspaceRepository.getUserIdByWorkspaceId(document.getWorkspaceId());
            for (UUID userId : usersId) {
                Boolean isDocumentOwner = documentRepository.isDocumentOwner(userId, document.getDocumentId());
                if (isDocumentOwner == null) {
                    Boolean isWorkspaceOwner = workspaceRepository.isOwnerWorkspace(document.getWorkspaceId(), userId);
                    if (isWorkspaceOwner) {
                        documentRepository.addUserIdDocumentIdToUserDocument(userId, document.getDocumentId(), "VIEWER");
                    } else {
                        documentRepository.addUserIdDocumentIdToUserDocument(userId, document.getDocumentId(), "NO_ACCESS");
                    }
                }
            }
            return document;
        } else {
            throw new BadRequestException("Your accessibility cannot create document");
        }
    }

    @Override
    public Document editDocument(UUID documentId, String title) {
        if (documentId == null) {
            throw new BadRequestException("Document id cannot be null");
        } else if (documentId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank or empty");
        }
        Document documentData = documentRepository.getDocumentByDocumentId(documentId);
        if (documentData == null) {
            throw new NotFoundException("Document doesn't exist");
        } else {
            History history = historyRepository.backUpDocument(documentData.getTitle(), LocalDateTime.now(), documentData.getStatus(), userAuthenticationService.getUserIdOfCurrentUser(), documentData.getDocumentId(), documentData.getWorkspaceId());
            List<Document> documents = documentRepository.getDocumentIdByPageId(documentData.getDocumentId());
            for (Document document : documents) {
                historyRepository.insertHistoryIdAndPageIdToHistoryPage(history.getHistoryId(), document.getDocumentId());
            }
            List<Block> blocks = blockRepository.getBlockByDocumentId(documentData.getDocumentId());
            for (Block block : blocks) {
                blockHistoryRepository.backUpBlock(block.getBlockType(), block.getContent(), block.getOrder(), history.getHistoryId());
            }
            String checkAccessibility = documentRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), documentId);
            if (!Objects.equals(checkAccessibility, "EDITOR")) {
                throw new NotEditorException("Your accessibility is not editor");
            }
            Document document = documentRepository.editDocument(documentId, title);
            documentRepository.deleteTagIdAndDocumentIdInTagDocument(documentId);
            return document;
        }
    }

    @Override
    public void currentEditing(UUID documentId) {
        if (documentId == null) {
            throw new BadRequestException("Document id cannot be null");
        } else if (documentId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank or empty");
        }
        Document document = documentRepository.getDocumentByDocumentId(documentId);
        if (document == null) {
            throw new NotFoundException("Document doesn't exist");
        } else {
            String checkAccessibility = documentRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId());
            if (Objects.equals(checkAccessibility, "VIEWER") || Objects.equals(checkAccessibility, "NO_ACCESS")) {
                throw new NotEditorException("Your accessibility cannot set current editing on this document");
            } else {
                documentRepository.currentEditing(documentId);
            }
        }
    }

    @Override
    public void setAccessibility(UUID documentId, UUID userId, UUID workspaceId, EAccessibility accessibility) {

        if (documentId == null) {
            throw new BadRequestException("Document id cannot be null");
        } else if (userId == null) {
            throw new BadRequestException("User id cannot be null");
        } else if (documentId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank or empty");
        } else if (userId.toString().isBlank()) {
            throw new BadRequestException("User id cannot be blank or empty");
        }

        User user = userRepository.getUserByUserIdAndDocumentId(userId, documentId);

        if(user == null){
            throw new NotFoundException("User not found for this document");
        } else {
            Document document = documentRepository.getDocumentByDocumentId(documentId);
            if (document == null) {
                throw new NotFoundException("Document doesn't exist");
            } else {
                Boolean isOwner = documentRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), documentId);
                if (isOwner) {
                    boolean isTrue = false;
                    for (EAccessibility access : EAccessibility.values()) {
                        if (accessibility.toString().equalsIgnoreCase(access.name())) {
                            isTrue = true;
                            break;
                        }
                    }
                    if (!isTrue) {
                        throw new BadRequestException("This accessibility is not correct : 'String' , " +
                                "please input one of (EDITOR, VIEWER and NO_ACCESS)");
                    } else if (accessibility.toString().isBlank()) {
                        throw new BadRequestException("This field could not empty");
                    }
                    documentRepository.setAccessibility(documentId, userId, workspaceId, accessibility);
                } else {
                    throw new NotOwnerException("You are not owner");
                }
            }
        }
    }

    @Override
    public Document viewDocument(UUID documentId) {

        Document document = documentRepository.getDocumentByDocumentId(documentId);
        if (document == null) {
            throw new NotFoundException("Document doesn't exist");
        } else {
            String checkAccessibility = documentRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId());
            if (Objects.equals(checkAccessibility, "NO_ACCESS")) {
                throw new NotEditorException("Your accessibility is NO_ACCESS so you cannot view this document");
            } else {
                return documentRepository.viewDocument(documentId);
            }
        }
    }

    @Override
    public List<Document> getDocumentInEachWorkspace(UUID workspaceId, Integer pageNo, Integer pageSize) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        pageNo = (pageNo - 1) * pageSize;
        List<Document> documents = documentRepository.getDocumentInEachWorkspace(workspaceId, pageNo, pageSize);
        if (documents.isEmpty()) {
            throw new NotFoundException("Empty document");
        }
        return documents;
    }

    @Override
    public Document duplicateDocument(UUID documentId) {
        if (documentId == null) {
            throw new BadRequestException("Document id cannot be null");
        } else if (documentId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank or empty");
        }
        Document documentData = documentRepository.getDocumentByDocumentId(documentId);
        if (documentData == null) {
            throw new NotFoundException("Document doesn't exist");
        } else {

            String checkAccessibility = documentRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), documentData.getDocumentId());

            if (Objects.equals(checkAccessibility, "VIEWER") || Objects.equals(checkAccessibility, "NO_ACCESS")) {
                throw new NotEditorException("Your accessibility cannot duplicate this document");
            } else {
                Document document = documentRepository.duplicateDocument(documentId);
                documentRepository.addDataToUserDocument(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId());
                List<Tag> tags = tagRepository.duplicateTag(documentId);
                for (Tag tag : tags) {
                    documentRepository.InsertTagIdAndDocumentIdIntoTagDocument(tag.getTagId(), document.getDocumentId());
                }
                document.setTags(tags);
                List<Block> blocks = blockRepository.duplicateBlock(documentId);
                for (Block block : blocks) {
                    blockRepository.updateDocumentIdForDuplicateBlock(document.getDocumentId(), block.getBlockId());
                }
                return document;
            }
        }
    }

    @Override
    public List<Document> searchDocumentByTagName(UUID workspaceId, String tagName) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (tagName == null) {
            throw new BadRequestException("Tag name cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        } else if (tagName.isBlank()) {
            throw new BadRequestException("Tag name cannot be blank or empty");
        }
        List<Document> documents = documentRepository.searchDocumentByTagName(workspaceId, tagName);
        if (documents.isEmpty()) {
            throw new NotFoundException("Empty document");
        }
        return documents;
    }

    @Override
    public void deleteDocument(UUID documentId) {
        Document document = documentRepository.getDocumentByDocumentId(documentId);
        if (document == null) {
            throw new NotFoundException("Document doesn't exist");
        } else {
            Boolean isOwner = documentRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), documentId);
            if (isOwner) {
                documentRepository.deleteDocument(documentId);
            } else {
                throw new NotOwnerException("You are not owner");
            }
        }
    }

    @Override
    public Document getDocumentByDocumentId(UUID documentId) {
        if (documentId == null) {
            throw new BadRequestException("Document id cannot be null");
        } else if (documentId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank or empty");
        }
        Document document = documentRepository.getDocumentByDocumentId(documentId);
        if (document == null) {
            throw new NotFoundException("Document doesn't exist");
        }
        return document;
    }

    @Override
    public Set<Document> searchDocumentByManyTagName(UUID workspaceId, List<String> tags) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        Set<Document> documents = documentRepository.searchDocumentByManyTagName(workspaceId, tags);
        if (documents.isEmpty()) {
            throw new NotFoundException("Empty document");
        }
        return documents;
    }

    @Override
    public List<MemberResponse> getAllMemberInEachDocument(UUID documentId) {
        return documentRepository.getAllMemberInEachDocument(documentId);
    }

}
