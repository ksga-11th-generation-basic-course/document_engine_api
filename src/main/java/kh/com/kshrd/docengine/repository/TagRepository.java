package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.model.entity.Tag;
import kh.com.kshrd.docengine.model.request.TagRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TagRepository {

    @Results(id = "tagMap", value = {
            @Result(property = "tagId", column = "tag_id"),
            @Result(property = "tagName", column = "tag_name"),
            @Result(property = "workspaceId", column = "workspace_id")
    })
    @Select("INSERT INTO tags(tag_name, workspace_id) VALUES (#{t.tagName}, #{t.workspaceId}) RETURNING *;")
    Tag createTag(@Param("t") TagRequest tagRequest);

    @ResultMap("tagMap")
    @Select("SELECT tags.tag_id, tag_name, workspace_id FROM tags INNER JOIN tag_document td on tags.tag_id = td.tag_id WHERE document_id = #{documentId};")
    List<Tag> getTagFromTagDocument(UUID documentId);

    @ResultMap("tagMap")
    @Select("UPDATE tags SET tag_name = #{tagName} WHERE tag_id = #{tagId} RETURNING *;")
    Tag editTag(UUID tagId, String tagName);

    @ResultMap("tagMap")
    @Delete("DELETE FROM tag_document WHERE tag_id = #{tagId} AND document_id = #{documentId};")
    void deleteTag(UUID tagId, UUID documentId);

    @ResultMap("tagMap")
    @Select("SELECT * FROM tags;")
    List<Tag> getAllTag();

    @ResultMap("tagMap")
    @Select("SELECT * FROM tags WHERE workspace_id = #{workspaceId}")
    List<Tag> getTagInEachWorkspace(UUID workspaceId);

    @ResultMap("tagMap")
    @Select("SELECT * FROM tags WHERE tag_id = #{tagId}")
    Tag getTagByTagId(UUID tagId);

    @ResultMap("tagMap")
    @Select("SELECT * FROM tags WHERE tag_id = #{tagId} AND workspace_id = #{workspaceId};")
    Tag getTagByTagIdAndWorkspaceId(UUID tagId, UUID workspaceId);

    @ResultMap("tagMap")
    @Select("SELECT td.tag_id, tag_name, workspace_id FROM tags INNER JOIN tag_document td on tags.tag_id = td.tag_id WHERE document_id = #{documentId};")
    List<Tag> duplicateTag(UUID documentId);

    @Insert("INSERT INTO tag_document(tag_id, document_id) VALUES(#{tagId}, #{documentId});")
    void insertTagIdAndDocumentIdIntoTagDocument(UUID tagId, UUID documentId);
}
