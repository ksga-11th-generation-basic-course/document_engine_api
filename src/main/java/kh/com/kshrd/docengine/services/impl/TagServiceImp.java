package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotDuplicateException;
import kh.com.kshrd.docengine.exceptions.NotEditorException;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.model.entity.Document;
import kh.com.kshrd.docengine.model.entity.Tag;
import kh.com.kshrd.docengine.model.request.TagRequest;
import kh.com.kshrd.docengine.repository.DocumentRepository;
import kh.com.kshrd.docengine.repository.TagRepository;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import kh.com.kshrd.docengine.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TagServiceImp implements TagService {

    private final TagRepository tagRepository;
    private final DocumentRepository documentRepository;
    private final UserAuthenticationService userAuthenticationService;

    @Override
    public Tag createTag(TagRequest tagRequest) {
        exception(tagRequest);

        String checkAccessibility = documentRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), tagRequest.getDocumentId());
        if (!Objects.equals(checkAccessibility, "EDITOR")) {
            throw new NotEditorException("Your accessibility is not editor");
        } else {
            List<Tag> tags = tagRepository.getAllTag();
            for (Tag tag : tags) {
                if (tag.getTagName().equals(tagRequest.getTagName()) && tag.getWorkspaceId().equals(tagRequest.getWorkspaceId())) {
                    throw new NotDuplicateException("This tag has already");
                }
            }
            Document document = documentRepository.getDocumentByDocumentIdAndWorkspaceId(tagRequest.getDocumentId(), tagRequest.getWorkspaceId());
            if (document == null) {
                throw new NotFoundException("Document not found in this workspace");
            }
            Tag tag = tagRepository.createTag(tagRequest);
            tagRepository.insertTagIdAndDocumentIdIntoTagDocument(tag.getTagId(), document.getDocumentId());
            return tag;
        }
    }

    @Override
    public Tag editTag(UUID tagId, String tagName) {
        if (tagId == null) {
            throw new BadRequestException("Tag id cannot be null");
        } else if (tagName == null) {
            throw new BadRequestException("Tag name cannot be null");
        } else if (tagId.toString().isBlank()) {
            throw new BadRequestException("Tag id cannot be blank or empty");
        } else if (tagName.isBlank()) {
            throw new BadRequestException("Tag name cannot be blank or empty");
        }
        Tag tag = tagRepository.getTagByTagId(tagId);
        if (tag == null) {
            throw new NotFoundException("Tag doesn't exist");
        } else {
            return tagRepository.editTag(tagId, tagName);
        }
    }

    @Override
    public void deleteTag(UUID tagId, UUID documentId) {
        if (tagId == null) {
            throw new BadRequestException("Tag id cannot be null");
        } else if (tagId.toString().isBlank()) {
            throw new BadRequestException("Tag id cannot be blank or empty");
        }
        Tag tag = tagRepository.getTagByTagId(tagId);
        if (tag == null) {
            throw new NotFoundException("Tag doesn't exist");
        } else {
            tagRepository.deleteTag(tagId, documentId);
        }
    }

    @Override
    public List<Tag> getAllTag() {
        List<Tag> tags = tagRepository.getAllTag();
        if (tags.isEmpty()) {
            throw new NotFoundException("Empty document");
        }
        return tags;
    }

    @Override
    public List<Tag> getTagInEachWorkspace(UUID workspaceId) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        return tagRepository.getTagInEachWorkspace(workspaceId);
    }

    @Override
    public UUID addTagsForDocument(UUID tagId, UUID documentId, UUID workspaceId) {
        if (tagId == null) {
            throw new BadRequestException("Tag id cannot be null");
        } else if (documentId == null) {
            throw new BadRequestException("Document id cannot be null");
        } else if (tagId.toString().isBlank()) {
            throw new BadRequestException("Tag id cannot be blank or empty");
        } else if (documentId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank or empty");
        }

        String checkAccessibility = documentRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), documentId);

        if (!Objects.equals(checkAccessibility, "EDITOR")) {
            throw new NotEditorException("Your accessibility is not editor");
        } else {
            Tag tag = tagRepository.getTagByTagIdAndWorkspaceId(tagId, workspaceId);
            Document document = documentRepository.getDocumentByDocumentIdAndWorkspaceId(documentId, workspaceId);

            if (tag == null) {
                throw new NotFoundException("Tag not found in this workspace");
            }

            if (document == null) {
                throw new NotFoundException("Document not found in this workspace");
            }

            List<Tag> tags = tagRepository.getTagFromTagDocument(documentId);
            for (Tag tagData : tags) {
                if (tagData.getTagId().equals(tagId)) {
                    throw new NotDuplicateException("This tag has already");
                }
            }

            return tagRepository.addTagsForDocument(tag.getTagId(), document.getDocumentId());
        }

    }

    @Override
    public List<Tag> getTagFromTagDocument(UUID documentId) {
        return tagRepository.getTagFromTagDocument(documentId);
    }

    @Override
    public Tag getTagByTagId(UUID tag, UUID workspaceId) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (tag == null) {
            throw new BadRequestException("Tag id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        } else if (tag.toString().isBlank()) {
            throw new BadRequestException("Tag id cannot be blank or empty");
        }

        Tag tagData = tagRepository.getTagByTagIdAndWorkspaceId(tag, workspaceId);
        if (tagData == null) {
            throw new NotFoundException("Tag not found in this workspace");
        }
        return tagData;
    }

    @Override
    public List<Tag> getTagFromHistoryId(UUID historyId) {
        return tagRepository.getTagFromHistoryId(historyId);
    }

    private void exception(TagRequest tagRequest) {
        if (tagRequest.getTagName() == null) {
            throw new BadRequestException("Tag name cannot be null");
        } else if (tagRequest.getWorkspaceId() == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (tagRequest.getTagName().isBlank()) {
            throw new BadRequestException("Tag name cannot be blank or empty");
        } else if (tagRequest.getWorkspaceId().toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
    }
}