package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.model.Tag;
import kh.com.kshrd.docengine.model.request.TagRequest;
import kh.com.kshrd.docengine.repository.TagRepository;
import kh.com.kshrd.docengine.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TagServiceImp implements TagService {

    private final TagRepository tagRepository;
    @Override
    public Tag createTag(TagRequest tagRequest) {
        return tagRepository.createTag(tagRequest);
    }

    @Override
    public Tag editTag(UUID tagId, TagRequest tagRequest) {
        return tagRepository.editTag(tagId, tagRequest);
    }

    @Override
    public void deleteTag(UUID tagId) {
        tagRepository.deleteTag(tagId);
    }
}
