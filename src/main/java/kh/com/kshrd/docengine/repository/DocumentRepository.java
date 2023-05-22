package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.model.Document;
import kh.com.kshrd.docengine.model.request.DocumentRequest;

import kh.com.kshrd.docengine.repository.provider.DocumentSqlProvider;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper
public interface DocumentRepository {
    @Results(id = "documentMap", value = {
            @Result(property = "documentId", column = "document_id"),
            @Result(property = "createdDate", column = "created_date"),
            @Result(property = "pageId", column = "page_id"),
            @Result(property = "workspaceId", column = "workspace_id"),
            @Result(property = "tags", column = "document_id", many = @Many(select = "kh.com.kshrd.docengine.repository.TagRepository.getTagFromTagDocument"))
    })
    @Select("INSERT INTO documents(title, created_date, page_id, workspace_id) VALUES (#{d.title}, #{d.createdDate}, #{d.pageId}, #{d.workspaceId}) RETURNING *;")
    Document createDocument(@Param("d") DocumentRequest documentRequest);

    @Select("INSERT INTO user_document(user_id, document_id, is_owner ,accessibility_status) VALUES (#{userIdOfCurrentUser}, #{documentId}, default, 'Editor');")
    void addDataToUserDocument(UUID userIdOfCurrentUser, UUID documentId);

    @ResultMap("documentMap")
    @Select("UPDATE documents SET title = #{title}, status = false WHERE document_id  = #{documentId} RETURNING *;")
    Document editDocument(UUID documentId, String title);

    @ResultMap("documentMap")
    @Update("UPDATE documents SET status = true WHERE document_id = #{documentId};")
    void currentEditing(UUID documentId);

    @Select("SELECT accessibility_status FROM user_document WHERE user_id = #{userIdOfCurrentUser} AND document_id = #{documentId};")
    String checkAccessibility(UUID userIdOfCurrentUser, UUID documentId);

    @Update("UPDATE user_document SET accessibility_status = #{accessibility} WHERE document_id = #{documentId} AND user_id = #{userId};")
    void setAccessibility(UUID documentId, UUID userId, String accessibility);

    @Select("SELECT is_owner FROM user_document WHERE user_id = #{userIdOfCurrentUser} AND document_id = #{documentId};")
    Boolean checkIsOwner(UUID userIdOfCurrentUser, UUID documentId);
    @ResultMap("documentMap")
    @Select("SELECT * FROM documents WHERE document_id = #{documentId};")
    Document getDocumentByDocumentId(UUID documentId);

    @ResultMap("documentMap")
    @Select("SELECT * FROM documents WHERE document_id = #{documentId};")
    Document viewDocument(UUID documentId);

    @ResultMap("documentMap")
    @Select("SELECT * FROM documents WHERE workspace_id = #{workspaceId} LIMIT #{pageSize} OFFSET #{pageNo};")
    List<Document> getDocumentInEachWorkspace(UUID workspaceId, Integer pageNo, Integer pageSize);

    @ResultMap("documentMap")
    @Select("INSERT INTO documents(title, created_date, page_id, workspace_id) SELECT title, created_date, page_id, workspace_id FROM documents WHERE document_id = #{documentId} RETURNING *;")
    Document duplicateDocument(UUID documentId);

    @Insert("INSERT INTO tag_document(tag_id, document_id) VALUES (#{tagId}, #{documentId});")
    void InsertTagIdAndDocumentIdIntoTagDocument(UUID tagId, UUID documentId);

    @ResultMap("documentMap")
    @Select("SELECT d.document_id, title, status, created_date, page_id, d.workspace_id FROM tags INNER JOIN tag_document td ON tags.tag_id = td.tag_id INNER JOIN documents d ON d.document_id = td.document_id WHERE d.workspace_id = #{workspaceId} AND tag_name = #{tagName};")
    List<Document> searchDocumentByTagName(UUID workspaceId, String tagName);

    @ResultMap("documentMap")
    @Delete("DELETE FROM documents WHERE document_id = #{documentId};")
    void deleteDocument(UUID documentId);

    @Delete("DELETE FROM tag_document WHERE document_id = #{documentId};")
    void deleteTagIdAndDocumentIdInTagDocument(UUID documentId);

    @ResultMap("documentMap")
    @SelectProvider(type = DocumentSqlProvider.class, method = "getDocumentsByWorkspaceAndTags")
    Set<Document> searchDocumentByManyTagName(UUID workspaceId, List<String> tags);

    @ResultMap("documentMap")
    @Select("UPDATE documents SET title = history.title FROM (SELECT title FROM histories WHERE history_id = #{historyId}) AS history WHERE document_id = #{documentId} RETURNING *;")
    Document restoreDocument(UUID historyId, UUID documentId);
}
