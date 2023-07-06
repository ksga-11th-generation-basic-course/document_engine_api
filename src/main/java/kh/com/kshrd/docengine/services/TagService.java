package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.entity.Tag;
import kh.com.kshrd.docengine.model.request.TagRequest;

import java.util.List;
import java.util.UUID;

public interface TagService {
    Tag createTag(TagRequest tagRequest);

    Tag editTag(UUID tagId, String tagName);

    void deleteTag(UUID tagId, UUID documentId);

    List<Tag> getAllTag();

    List<Tag> getTagInEachWorkspace(UUID workspaceId);

    UUID addTagsForDocument(UUID tagId, UUID documentId, UUID workspaceId);

    List<Tag> getTagFromTagDocument(UUID documentId);

    Tag getTagByTagId(UUID tag, UUID workspaceId);
}