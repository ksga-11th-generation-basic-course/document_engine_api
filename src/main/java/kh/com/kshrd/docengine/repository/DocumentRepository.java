package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.enums.EAccessibility;
import kh.com.kshrd.docengine.model.entity.Document;
import kh.com.kshrd.docengine.model.request.DocumentRequest;

import kh.com.kshrd.docengine.model.response.DocumentResponse;
import kh.com.kshrd.docengine.model.response.MemberResponse;
import kh.com.kshrd.docengine.repository.provider.DocumentSqlProvider;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper
public interface DocumentRepository {
    @Results(id = "documentMap", value = {
            @Result(property = "documentId", column = "document_id"),
            @Result(property = "createdDate", column = "created_date"),
            @Result(property = "pages", column = "document_id", many = @Many(select = "getPageByPageId")),
            @Result(property = "pageId", column = "page_id"),
            @Result(property = "workspaceId", column = "workspace_id"),
            @Result(property = "tags", column = "document_id", many = @Many(select = "kh.com.kshrd.docengine.repository.TagRepository.getTagFromTagDocument")),
    })
    @Select("INSERT INTO documents(title, created_date, page_id, workspace_id) VALUES (#{d.title}, #{d.createdDate}, #{d.pageId}, #{d.workspaceId}) RETURNING *;")
    Document createDocument(@Param("d") DocumentRequest documentRequest);

    @Select("INSERT INTO user_document(user_id, document_id, is_owner ,accessibility_status) VALUES (#{userIdOfCurrentUser}, #{documentId}, true, 'EDITOR');")
    void addDataToUserDocument(UUID userIdOfCurrentUser, UUID documentId);

    @ResultMap("documentMap")
    @Select("UPDATE documents SET title = #{title}, status = false WHERE document_id  = #{documentId} RETURNING *;")
    Document editDocument(UUID documentId, String title);

    @ResultMap("documentMap")
    @Select("UPDATE documents SET status = #{status} WHERE document_id = #{documentId} RETURNING *;")
    Document currentEditing(UUID documentId, Boolean status);

    @Select("SELECT accessibility_status FROM user_document WHERE user_id = #{userIdOfCurrentUser} AND document_id = #{documentId};")
    String checkAccessibility(UUID userIdOfCurrentUser, UUID documentId);

    @Update("UPDATE user_document SET accessibility_status = #{accessibility} FROM documents WHERE documents.document_id = #{documentId} AND user_id = #{userId} AND workspace_id = #{workspaceId};")
    void setAccessibility(UUID documentId, UUID userId, UUID workspaceId, EAccessibility accessibility);

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

    @Results(id = "userDocumentMap", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "isOwner", column = "is_owner"),
            @Result(property = "accessibility", column = "accessibility_status")
    })
    @Select("SELECT ud.user_id, username, is_owner, accessibility_status FROM users INNER JOIN user_document ud on users.user_id = ud.user_id WHERE document_id = #{documentId};")
    List<MemberResponse> getAllMemberInEachDocument(UUID documentId);

    @ResultMap("documentMap")
    @Select("SELECT * FROM documents WHERE page_id = #{documentId};")
    List<Document> getDocumentIdByPageId(UUID documentId);

    @ResultMap("documentMap")
    @Select("SELECT * FROM documents WHERE page_id = #{documentId}")
    List<Document> getPageByPageId(UUID documentId);

    @Delete("""
            DELETE FROM user_document WHERE user_id = '96b4d46d-8515-4a9e-b13b-799165e20aa1';
            """)
    void deleteDocumentFromUserDocument(UUID userId);

    @ResultMap("documentMap")
    @Select("SELECT * FROM documents WHERE workspace_id = #{workspaceId};")
    List<Document> getDocumentByWorkspaceId(UUID workspaceId);

    @Insert("INSERT INTO user_document(user_id, document_id, accessibility_status) VALUES (#{userIdOfCurrentUser}, #{documentId}, #{accessibility})")
    void addUserIdDocumentIdToUserDocument(UUID userIdOfCurrentUser, UUID documentId, String accessibility);

    @Select("SELECT is_owner FROM user_document WHERE user_id = #{userId} AND document_id = #{documentId} AND is_owner = true;")
    Boolean isDocumentOwner(UUID userId, UUID documentId);

    @ResultMap("documentMap")
    @Select("SELECT * FROM documents WHERE document_id = #{documentId} AND workspace_id = #{workspaceId};")
    Document getDocumentByDocumentIdAndWorkspaceId(UUID documentId, UUID workspaceId);

    @Select("SELECT EXISTS(SELECT * FROM documents WHERE document_id = #{pageId});")
    Boolean checkPageIsExits(UUID pageId);

    @Select("SELECT EXISTS(SELECT * FROM users INNER JOIN user_document ud on users.user_id = ud.user_id WHERE ud.user_id = #{userIdOfCurrentUser} AND document_id = #{documentId});")
    Boolean checkUserIsMemberOfTheDocument(UUID userIdOfCurrentUser, UUID documentId);

    @Select("SELECT edited_date FROM documents INNER JOIN histories h on documents.document_id = h.document_id WHERE h.document_id = #{documentId} ORDER BY edited_date DESC LIMIT 1;")
    LocalDateTime getEditDate(UUID documentId);

    @Select("SELECT username from users inner join user_document ud on users.user_id = ud.user_id where document_id=#{documentId} and is_owner=true")
    String getUsernameByDocumentId(UUID documentId);

    @Select("SELECT workspace_name from documents inner join workspaces w on w.workspace_id = documents.workspace_id where document_id=#{documentId}")
    String getWorkspaceNameByDocumentId(UUID documentId);

    @Results(id = "userDocumentMaps", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "isOwner", column = "is_owner"),
            @Result(property = "profileImage", column = "profile_image"),
            @Result(property = "accessibility", column = "accessibility_status")
    })
    @Select("SELECT ud.user_id,username,email,profile_image,is_owner,accessibility_status FROM users INNER JOIN user_document ud on users.user_id = ud.user_id WHERE ud.user_id= #{userId} AND document_id = #{documentId};")
    MemberResponse getUserByUserDocument(UUID userId, UUID documentId);

    @ResultMap("documentMap")
    @Select("SELECT DISTINCT d.document_id,d.title,d.status,d.created_date,d.page_id,d.workspace_id from documents d INNER JOIN histories h on d.document_id = h.document_id where edited_by=#{userIdOfCurrentUser} ORDER BY d.created_date DESC LIMIT 3")
    List<Document> getDocumentRecently(UUID userIdOfCurrentUser);

    @ResultMap("documentMap")
    @Select("SELECT * FROM documents WHERE page_id = #{pageId};")
    List<Document> getPageInEachDocument(UUID pageId);

    @ResultMap("documentMap")
    @Select("SELECT * FROM documents WHERE document_id = #{documentId};")
    Document getDocumentByPageId(UUID pageId);

    @ResultMap("documentMap")
    @Select("SELECT * FROM documents WHERE workspace_id = #{workspaceId};")
    List<Document> getAllDocumentByWorkspaceId(UUID workspaceId);
}


