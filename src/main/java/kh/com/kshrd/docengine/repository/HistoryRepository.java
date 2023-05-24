package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.model.entity.History;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface HistoryRepository {
    @Results(id = "historyMap", value = {
            @Result(property = "historyId", column = "history_id"),
            @Result(property = "editedDate", column = "edited_date"),
            @Result(property = "editedBy", column = "edited_by", one = @One(select = "kh.com.kshrd.docengine.repository.UserRepository.getUserNameByUserId")),
            @Result(property = "documentId", column = "document_id"),
            @Result(property = "workspaceId", column = "workspace_id")
    })
    @Select("SELECT * FROM histories WHERE document_id = #{documentId}")
    List<History> getHistoryInEachDocument(UUID documentId);

    @ResultMap("historyMap")
    @Select("INSERT INTO histories(title, edited_date, status, edited_by, document_id, page_id, workspace_id) VALUES (#{title}, #{now}, #{status}, #{userIdOfCurrentUser}, #{documentId}, #{pageId}, #{workspaceId}) RETURNING *")
    History backUpDocument(String title, LocalDateTime now, Boolean status, UUID userIdOfCurrentUser, UUID documentId, UUID pageId, UUID workspaceId);
}