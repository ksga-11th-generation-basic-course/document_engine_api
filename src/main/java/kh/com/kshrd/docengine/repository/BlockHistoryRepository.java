package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.configuration.JsonTypeHandler;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;

import java.util.Map;
import java.util.UUID;

@Mapper
public interface BlockHistoryRepository {
    @Results(id = "blockMap", value = {
            @Result(property = "blockHistoryId", column = "history_block_id"),
            @Result(property = "blockType", column = "block_type"),
            @Result(property = "content", column = "block_content", typeHandler = JsonTypeHandler.class),
            @Result(property = "order", column = "block_order"),
            @Result(property = "historyId", column = "history_id")
    })
    @Insert("INSERT INTO history_block(history_block_type, history_block_content, history_block_order, history_id) VALUES (#{blockType}, #{content, typeHandler = kh.com.kshrd.docengine.configuration.JsonTypeHandler}::JSON, #{order}, #{historyId});")
    void backUpBlock(String blockType, Map<String, Object> content, Integer order, UUID historyId);
}
