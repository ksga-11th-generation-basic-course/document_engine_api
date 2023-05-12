package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.configuration.JsonTypeHandler;
import kh.com.kshrd.docengine.model.Block;
import kh.com.kshrd.docengine.model.request.BlockRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface BlockRepository {
    @Results(id = "blockMap", value = {
            @Result(property = "blockId", column = "block_id"),
            @Result(property = "blockType", column = "block_type"),
            @Result(property = "content", column = "block_content", typeHandler = JsonTypeHandler.class),
            @Result(property = "order", column = "block_order"),
            @Result(property = "documentId", column = "document_id", one = @One(select = "kh.com.kshrd.docengine.repository.DocumentRepository.getDocumentByDocumentId"))
    })
    @Select("INSERT INTO blocks(block_type, block_content, block_order, document_id) VALUES (#{d.blockType}, #{d.content, typeHandler = kh.com.kshrd.docengine.configuration.JsonTypeHandler}::JSON , default, #{d.documentId}) RETURNING *;")
    Block createBlock(@Param("d") BlockRequest blockRequest);

    @ResultMap("blockMap")
    @Select("SELECT * FROM blocks;")
    List<Block> getBlockData();

    @ResultMap("blockMap")
    @Select("UPDATE blocks SET block_content = #{content} WHERE block_id = #{blockId} RETURNING *;")
    Block editBlock(UUID blockId, String content);

    @ResultMap("blockMap")
    @Delete("DELETE FROM blocks WHERE block_id = #{blockId};")
    void deleteBlock(UUID blockId);

    @ResultMap("blockMap")
    @Select("INSERT INTO blocks(block_type, block_content, document_id) SELECT block_type, block_content, document_id FROM blocks WHERE document_id = #{documentId} RETURNING *;")
    List<Block> duplicateBlock(UUID documentId);

    @ResultMap("blockMap")
    @Update("UPDATE blocks SET document_id = #{documentId} WHERE block_id  = #{blockId};")
    void updateDocumentIdForDuplicateBlock(UUID documentId, UUID blockId);
}

