package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.Tag;
import kh.com.kshrd.docengine.model.request.TagRequest;

import java.util.UUID;

public interface TagService {
    Tag createTag(TagRequest tagRequest);

    Tag editTag(UUID tagId, TagRequest tagRequest);

    void deleteTag(UUID tagId);
}
