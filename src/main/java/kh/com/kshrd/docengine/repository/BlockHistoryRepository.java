package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.configuration.JsonTypeHandler;
import kh.com.kshrd.docengine.model.entity.BlockHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper
public interface BlockHistoryRepository {

    @Insert("INSERT INTO history_block(history_block_type, history_block_content, history_block_order, history_id) VALUES (#{blockType}, #{content, typeHandler = kh.com.kshrd.docengine.configuration.JsonTypeHandler}::JSON, #{order}, #{historyId});")
    void backUpBlock(String blockType, Map<String, Object> content, Integer order, UUID historyId);

    @Results(id = "blockHistoryMap", value = {
            @Result(property = "blockHistoryId", column = "history_block_id"),
            @Result(property = "blockType", column = "history_block_type"),
            @Result(property = "content", column = "history_block_content", typeHandler = JsonTypeHandler.class),
            @Result(property = "order", column = "history_block_order"),
            @Result(property = "historyId", column = "history_id")
    })
    @Select("SELECT * FROM history_block WHERE history_id = #{historyId};")
    List<BlockHistory> getBlockForEachHistory(UUID historyId);
}
