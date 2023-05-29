package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.model.entity.Tag;
import kh.com.kshrd.docengine.model.request.TagRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TagRepository {

    /*create tag*/
    @Results(id = "tagMap", value = {
            @Result(property = "tagId", column = "tag_id"),
            @Result(property = "tagName", column = "tag_name"),
            @Result(property = "workspaceId", column = "workspace_id")
    })
    @Select("INSERT INTO tags(tag_name, workspace_id) VALUES (#{t.tagName}, #{t.workspaceId}) RETURNING *;")
    Tag createTag(@Param("t") TagRequest tagRequest);

    /*get tag from TagDocument*/
    @ResultMap("tagMap")
    @Select("SELECT tags.tag_id, tag_name, workspace_id FROM tags INNER JOIN tag_document td on tags.tag_id = td.tag_id WHERE document_id = #{documentId};")
    List<Tag> getTagFromTagDocument(UUID documentId);

    /*edit tag*/
    @ResultMap("tagMap")
    @Select("UPDATE tags SET tag_name = #{tagName} WHERE tag_id = #{tagId} RETURNING *;")
    Tag editTag(UUID tagId, String tagName);

    /*delete tag*/
    @ResultMap("tagMap")
    @Delete("DELETE FROM tags WHERE tag_id = #{tagId};")
    void deleteTag(UUID tagId);

    /*get all tag*/
    @ResultMap("tagMap")
    @Select("SELECT * FROM tags;")
    List<Tag> getAllTag();

   /* get tag in each workspace*/
    @ResultMap("tagMap")
    @Select("SELECT * FROM tags WHERE workspace_id = #{workspaceId}")
    List<Tag> getTagInEachWorkspace(UUID workspaceId);

    /* get tag by tagId*/
    @ResultMap("tagMap")
    @Select("SELECT * FROM tags WHERE tag_id = #{tagId}")
    Tag getTagByTagId(UUID tagId);
}
