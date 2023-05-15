package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.Tag;
import kh.com.kshrd.docengine.model.request.TagRequest;

import java.util.List;
import java.util.UUID;

public interface TagService {
    Tag createTag(TagRequest tagRequest);

    Tag editTag(UUID tagId, String tagName);

    void deleteTag(UUID tagId);

    List<Tag> getAllTag();

    List<Tag> getTagInEachWorkspace(UUID workspaceId);
}
