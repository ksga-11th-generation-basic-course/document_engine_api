package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.model.Tag;
import kh.com.kshrd.docengine.model.request.TagRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TagRepository {

    @Results(id = "tagMap", value = {
            @Result(property = "tagId", column = "tag_id"),
            @Result(property = "tagName", column = "tag_name")
    })
    @Select("INSERT INTO tags(tag_name) VALUES (#{t.tagName}) RETURNING *;")
    Tag createTag(@Param("t") TagRequest tagRequest);

    @ResultMap("tagMap")
    @Select("SELECT tags.tag_id, tag_name FROM tags INNER JOIN tag_document td on tags.tag_id = td.tag_id WHERE document_id = #{documentId};")
    List<Tag> getTagFromTagDocument(UUID documentId);

    @ResultMap("tagMap")
    @Select("UPDATE tags SET tag_name = #{t.tagName} WHERE tag_id = #{tagId} RETURNING *;")
    Tag editTag(UUID tagId,@Param("t") TagRequest tagRequest);

    @ResultMap("tagMap")
    @Delete("DELETE FROM tags WHERE tag_id = #{tagId};")
    void deleteTag(UUID tagId);
}
