package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.configuration.JsonTypeHandler;
import kh.com.kshrd.docengine.model.entity.Block;
import kh.com.kshrd.docengine.model.entity.History;
import kh.com.kshrd.docengine.model.request.BlockRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper
public interface BlockRepository {

    /*create block*/
    @Results(id = "blockMap", value = {
            @Result(property = "blockId", column = "block_id"),
            @Result(property = "blockType", column = "block_type"),
            @Result(property = "content", column = "block_content", typeHandler = JsonTypeHandler.class),
            @Result(property = "order", column = "block_order"),
            @Result(property = "documentId", column = "document_id")
    })
    @Select("INSERT INTO blocks(block_type, block_content, block_order, document_id) VALUES (#{d.blockType}, #{d.content, typeHandler = kh.com.kshrd.docengine.configuration.JsonTypeHandler}::JSON , #{order}, #{d.documentId}) RETURNING *;")
    Block createBlock(@Param("d") BlockRequest blockRequest, Integer order);

    /*edit block*/
    @ResultMap("blockMap")
    @Select("UPDATE blocks SET block_content = #{content, typeHandler = kh.com.kshrd.docengine.configuration.JsonTypeHandler}::JSON WHERE block_id = #{blockId} AND document_id = #{documentId} RETURNING *;")
    Block editBlock(UUID blockId, UUID documentId , Map<String, Object> content);

    /*delete block*/
    @ResultMap("blockMap")
    @Delete("DELETE FROM blocks WHERE block_id = #{blockId} AMD document_id = #{documentId};")
    void deleteBlock(UUID blockId, UUID documentId);

    /*duplicate block*/
    @ResultMap("blockMap")
    @Select("INSERT INTO blocks(block_type, block_content, document_id) SELECT block_type, block_content, document_id FROM blocks WHERE document_id = #{documentId} RETURNING *;")
    List<Block> duplicateBlock(UUID documentId);

    /*update document id for duplicate block*/
    @ResultMap("blockMap")
    @Update("UPDATE blocks SET document_id = #{documentId} WHERE block_id  = #{blockId};")
    void updateDocumentIdForDuplicateBlock(UUID documentId, UUID blockId);

    /*get block for each document*/
    @ResultMap("blockMap")
    @Select("SELECT * FROM blocks WHERE document_id = #{documentId};")
    List<Block> getBlockForEachDocument(UUID documentId);

    /*get block by document id*/
    @ResultMap("blockMap")
    @Select("SELECT * FROM blocks WHERE document_id = #{documentId};")
    List<Block> getBlockByDocumentId(UUID documentId);

    /*delete block by document id*/
    @Delete("DELETE FROM blocks WHERE document_id = #{documentId};")
    void deleteBlockByDocumentId(UUID documentId);

    /*restore block document*/
    @Insert("INSERT INTO blocks(block_type, block_content, block_order, document_id) SELECT history_block_type, history_block_content, history_block_order, document_id FROM history_block INNER JOIN histories h on history_block.history_id = h.history_id WHERE h.history_id = #{historyId};")
    void restoreBlockDocument(UUID historyId);

    /*get block by blockId*/
    @ResultMap("blockMap")
    @Select("SELECT * FROM blocks WHERE block_id = #{blockId};")
    Block getBlockByBlockId(UUID blockId);

    @Select("SELECT COUNT(*) FROM blocks WHERE document_id = #{documentId};")
    Integer order(UUID documentId);
}

