
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
        return documentRepository.getDocumentInEachWorkspace(workspaceId, pageNo, pageSize);
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

    @Override
    public String getUserByDocumentId(UUID documentId) {
        return documentRepository.getUsernameByDocumentId(documentId);
    }

    @Override
    public String getWorkspaceNameByDocumentId(UUID documentId) {
        return documentRepository.getWorkspaceNameByDocumentId(documentId);
    }

}

package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.enums.ESortCurrentDateTime;
import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotEditorException;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.exceptions.NotOwnerException;
import kh.com.kshrd.docengine.model.entity.*;
import kh.com.kshrd.docengine.enums.EAccessibility;
import kh.com.kshrd.docengine.model.request.DocumentRequest;
import kh.com.kshrd.docengine.model.response.DocumentResponse;
import kh.com.kshrd.docengine.model.response.MemberResponse;
import kh.com.kshrd.docengine.repository.*;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import kh.com.kshrd.docengine.services.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

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
//        Boolean page = documentRepository.checkPageIsExits(documentRequest.getPageId());
//        if(!page){
//            throw new NotFoundException("Page doesn't exist");
//        }

        Boolean isCheckAccessibility = workspaceRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), documentRequest.getWorkspaceId());
        if (isCheckAccessibility == null) {
            throw new NotFoundException("You are not a member in this workspace");
        } else if (isCheckAccessibility) {
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

        if (user == null) {
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
    public List<DocumentResponse> getDocumentInEachWorkspace(UUID workspaceId, Integer pageNo, Integer pageSize, ESortCurrentDateTime eSortCurrentDateTime) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        pageNo = (pageNo - 1) * pageSize;
        List<Document> documents = documentRepository.getDocumentInEachWorkspace(workspaceId, pageNo, pageSize);

        List<DocumentResponse> documentResponses = new ArrayList<>();
        for (Document document : documents) {
            DocumentResponse documentResponse = new DocumentResponse();
            LocalDateTime editDate = getEditDate(document.getDocumentId());
            documentResponse.setDocumentId(document.getDocumentId());
            documentResponse.setTitle(document.getTitle());
            documentResponse.setStatus(document.getStatus());
            documentResponse.setCreatedDate(document.getCreatedDate());
            documentResponse.setPages(document.getPages());
            documentResponse.setWorkspaceId(document.getWorkspaceId());
            documentResponse.setTags(document.getTags());
            documentResponse.setBlocks(document.getBlocks());
            documentResponse.setEditDate(recently(editDate));

            documentResponses.add(documentResponse);
        }

        boolean isSortTrue = false;
        for (ESortCurrentDateTime sortCurrentDateTime : ESortCurrentDateTime.values()) {
            if (eSortCurrentDateTime.toString().equalsIgnoreCase(sortCurrentDateTime.name())) {
                isSortTrue = true;
                break;
            }
        }
        if (!isSortTrue) {
            throw new BadRequestException("This sort by week, month and year are not correct : 'String' , " +
                    "please input one of (THIS_WEEK, THIS_MONTH and THIS_YEAR)");
        } else if (eSortCurrentDateTime.toString().isBlank()) {
            throw new BadRequestException("This field could not empty");
        }

        switch (eSortCurrentDateTime) {
            case THIS_WEEK -> {
                LocalDate now = LocalDate.now();
                LocalDate startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
                LocalDate endOfWeek = now.with(java.time.DayOfWeek.SUNDAY);

                return documentResponses.stream()
                        .filter(documentResponse -> {
                            LocalDate documentDate = documentResponse.getCreatedDate().toLocalDate();
                            return !documentDate.isBefore(startOfWeek) && !documentDate.isAfter(endOfWeek);
                        })
                        .collect(Collectors.toList());
            }
            case THIS_MONTH -> {
                YearMonth currentMonth = YearMonth.now();

                return documentResponses.stream()
                        .filter(documentResponse -> {
                            YearMonth documentMonth = YearMonth.from(documentResponse.getCreatedDate());
                            return documentMonth.equals(currentMonth);
                        })
                        .collect(Collectors.toList());
            }
            case THIS_YEAR -> {
                Year currentYear = Year.now();

                return documentResponses.stream()
                        .filter(documentResponse -> {
                            Year documentYear = Year.of(documentResponse.getCreatedDate().getYear());
                            return documentYear.equals(currentYear);
                        })
                        .collect(Collectors.toList());
            }
            default -> {
                return documentResponses;
            }
        }
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

    @Override
    public LocalDateTime getEditDate(UUID documentId) {
        return documentRepository.getEditDate(documentId);
    }

    public String recently(LocalDateTime editData) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(editData, now);

        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return minutes + " minutes ago";
        } else {
            long hours = duration.toHours();
            if (hours < 24) {
                return hours + " hours ago";
            } else {
                long days = duration.toDays();
                return days + " days ago";
            }
        }
    }

}
