package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotDuplicateException;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.model.entity.Tag;
import kh.com.kshrd.docengine.model.request.TagRequest;
import kh.com.kshrd.docengine.repository.TagRepository;
import kh.com.kshrd.docengine.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TagServiceImp implements TagService {

    private final TagRepository tagRepository;

    @Override
    public Tag createTag(TagRequest tagRequest) {
        exception(tagRequest);
        List<Tag> tags = tagRepository.getAllTag();
        for (Tag tag : tags) {
            if (tag.getTagName().equals(tagRequest.getTagName()) && tag.getWorkspaceId().equals(tagRequest.getWorkspaceId())) {
                throw new NotDuplicateException("This tag has already");
            }
        }
        Tag tag = tagRepository.createTag(tagRequest);
        tagRepository.insertTagIdAndDocumentIdIntoTagDocument(tag.getTagId(), tagRequest.getDocument_id());
        return tag;
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
    public void deleteTag(UUID tagId) {
        if (tagId == null) {
            throw new BadRequestException("Tag id cannot be null");
        } else if (tagId.toString().isBlank()) {
            throw new BadRequestException("Tag id cannot be blank or empty");
        }
        Tag tag = tagRepository.getTagByTagId(tagId);
        if (tag == null) {
            throw new NotFoundException("Tag doesn't exist");
        } else {
            tagRepository.deleteTag(tagId);
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
    public void addTagsForDocument(UUID tagId, UUID documentId) {
        if (tagId == null) {
            throw new BadRequestException("Tag id cannot be null");
        } else if (documentId == null) {
            throw new BadRequestException("Document id cannot be null");
        } else if (tagId.toString().isBlank()) {
            throw new BadRequestException("Tag id cannot be blank or empty");
        } else if (documentId.toString().isBlank()) {
            throw new BadRequestException("Document id cannot be blank or empty");
        }
        tagRepository.insertTagIdAndDocumentIdIntoTagDocument(tagId, documentId);
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
