package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.configuration.JsonTypeHandler;
import kh.com.kshrd.docengine.model.entity.Block;
import kh.com.kshrd.docengine.model.request.BlockRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper
public interface BlockRepository {
    @Results(id = "blockMap", value = {
            @Result(property = "blockId", column = "block_id"),
            @Result(property = "blockType", column = "block_type"),
            @Result(property = "content", column = "block_content", typeHandler = JsonTypeHandler.class),
            @Result(property = "order", column = "block_order"),
            @Result(property = "documentId", column = "document_id")
    })
    @Select("INSERT INTO blocks(block_type, block_content, block_order, document_id) VALUES (#{d.blockType}, #{d.content, typeHandler = kh.com.kshrd.docengine.configuration.JsonTypeHandler}::JSON , #{d.order}, #{d.documentId}) RETURNING *;")
    Block createBlock(@Param("d") BlockRequest blockRequest);

    @ResultMap("blockMap")
    @Select("UPDATE blocks SET block_content = #{content, typeHandler = kh.com.kshrd.docengine.configuration.JsonTypeHandler}::JSON WHERE block_id = #{blockId} RETURNING *;")
    Block editBlock(UUID blockId, Map<String, Object> content);

    @ResultMap("blockMap")
    @Delete("DELETE FROM blocks WHERE block_id = #{blockId};")
    void deleteBlock(UUID blockId);

    @ResultMap("blockMap")
    @Select("INSERT INTO blocks(block_type, block_content, document_id) SELECT block_type, block_content, document_id FROM blocks WHERE document_id = #{documentId} RETURNING *;")
    List<Block> duplicateBlock(UUID documentId);

    @ResultMap("blockMap")
    @Update("UPDATE blocks SET document_id = #{documentId} WHERE block_id  = #{blockId};")
    void updateDocumentIdForDuplicateBlock(UUID documentId, UUID blockId);

    @ResultMap("blockMap")
    @Select("SELECT * FROM blocks WHERE document_id = #{documentId};")
    List<Block> getBlockForEachDocument(UUID documentId);

    @ResultMap("blockMap")
    @Select("SELECT * FROM blocks WHERE document_id = #{documentId};")
    List<Block> getBlockByDocumentId(UUID documentId);

    @Delete("DELETE FROM blocks WHERE document_id = #{documentId};")
    void deleteBlockByDocumentId(UUID documentId);

    @Insert("INSERT INTO blocks(block_type, block_content, block_order, document_id) SELECT history_block_type, history_block_content, history_block_order, document_id FROM history_block INNER JOIN histories h on history_block.history_id = h.history_id WHERE h.history_id = #{historyId};")
    void restoreBlockDocument(UUID historyId);
}

