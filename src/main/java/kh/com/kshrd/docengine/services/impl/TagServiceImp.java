package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.NotDuplicateException;
import kh.com.kshrd.docengine.model.Tag;
import kh.com.kshrd.docengine.model.request.TagRequest;
import kh.com.kshrd.docengine.repository.TagRepository;
import kh.com.kshrd.docengine.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TagServiceImp implements TagService {

    private final TagRepository tagRepository;
    @Override
    public Tag createTag(TagRequest tagRequest) {
        List<Tag> tags = tagRepository.getAllTag();
        for (Tag tag : tags){
            if(tag.getTagName().equals(tagRequest.getTagName()) && tag.getWorkspaceId().equals(tagRequest.getWorkspaceId())){
                throw new NotDuplicateException("This tag has already");
            }
        }
        return tagRepository.createTag(tagRequest);
    }

    @Override
    public Tag editTag(UUID tagId, String tagName) {
        return tagRepository.editTag(tagId, tagName);
    }

    @Override
    public void deleteTag(UUID tagId) {
        tagRepository.deleteTag(tagId);
    }

    @Override
    public List<Tag> getAllTag() {
        return tagRepository.getAllTag();
    }

    @Override
    public List<Tag> getTagInEachWorkspace(UUID workspaceId) {
        return tagRepository.getTagInEachWorkspace(workspaceId);
    }
}
