package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.enums.EAccessibility;
import kh.com.kshrd.docengine.model.entity.Document;
import kh.com.kshrd.docengine.model.request.DocumentRequest;

import kh.com.kshrd.docengine.model.response.MemberResponse;
import kh.com.kshrd.docengine.model.response.UserResponse;
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
            @Result(property = "pages", column = "document_id", many = @Many(select = "getPageByPageId")),
            @Result(property = "workspaceId", column = "workspace_id"),
            @Result(property = "tags", column = "document_id", many = @Many(select = "kh.com.kshrd.docengine.repository.TagRepository.getTagFromTagDocument")),
            @Result(property = "blocks", column = "document_id", many = @Many(select = "kh.com.kshrd.docengine.repository.BlockRepository.getBlockByDocumentId"))
    })
    @Select("INSERT INTO documents(title, created_date, page_id, workspace_id) VALUES (#{d.title}, #{d.createdDate}, #{d.pageId}, #{d.workspaceId}) RETURNING *;")
    Document createDocument(@Param("d") DocumentRequest documentRequest);

    @Select("INSERT INTO user_document(user_id, document_id, is_owner ,accessibility_status) VALUES (#{userIdOfCurrentUser}, #{documentId}, true, 'EDITOR');")


    /*create document*/
    @Results(id = "documentMap", value = {@Result(property = "documentId", column = "document_id"), @Result(property = "createdDate", column = "created_date"), @Result(property = "pageId", column = "page_id"), @Result(property = "workspaceId", column = "workspace_id"), @Result(property = "tags", column = "document_id", many = @Many(select = "kh.com.kshrd.docengine.repository.TagRepository.getTagFromTagDocument"))})
    @Select("INSERT INTO documents(title, created_date, page_id, workspace_id) VALUES (#{d.title}, #{d.createdDate}, #{d.pageId}, #{d.workspaceId}) RETURNING *;")
    Document createDocument(@Param("d") DocumentRequest documentRequest);

    /*add data to user document*/
    @Select("INSERT INTO user_document(user_id, document_id, is_owner ,accessibility_status) VALUES (#{userIdOfCurrentUser}, #{documentId}, default, 'Editor');")

    void addDataToUserDocument(UUID userIdOfCurrentUser, UUID documentId);

    /*edit document*/
    @ResultMap("documentMap")
    @Select("UPDATE documents SET title = #{title}, status = false WHERE document_id  = #{documentId} RETURNING *;")
    Document editDocument(UUID documentId, String title);

    /*current editing document*/
    @ResultMap("documentMap")
    @Update("UPDATE documents SET status = true WHERE document_id = #{documentId};")
    void currentEditing(UUID documentId);

    /*check accessibility*/
    @Select("SELECT accessibility_status FROM user_document WHERE user_id = #{userIdOfCurrentUser} AND document_id = #{documentId};")
    String checkAccessibility(UUID userIdOfCurrentUser, UUID documentId);

    @Update("UPDATE user_document SET accessibility_status = 'EDITOR' FROM documents WHERE documents.document_id = #{documentId} AND user_id = #{userId} AND workspace_id = #{workspaceId};")
    void setAccessibility(UUID documentId, UUID userId, UUID workspaceId, EAccessibility accessibility);

    /*set accessibility*/
    @Update("UPDATE user_document SET accessibility_status = #{accessibility} WHERE document_id = #{documentId} AND user_id = #{userId};")
    void setAccessibility(UUID documentId, UUID userId, String accessibility);


    /*check IsOwner*/
    @Select("SELECT is_owner FROM user_document WHERE user_id = #{userIdOfCurrentUser} AND document_id = #{documentId};")
    Boolean checkIsOwner(UUID userIdOfCurrentUser, UUID documentId);



    /*get document by document id*/
    @ResultMap("documentMap")
    @Select("SELECT * FROM documents WHERE document_id = #{documentId};")
    Document getDocumentByDocumentId(UUID documentId);

    /*view document*/
    @ResultMap("documentMap")
    @Select("SELECT * FROM documents WHERE document_id = #{documentId};")
    Document viewDocument(UUID documentId);

    /*get document in each workspace*/
    @ResultMap("documentMap")
    @Select("SELECT * FROM documents WHERE workspace_id = #{workspaceId} LIMIT #{pageSize} OFFSET #{pageNo};")
    List<Document> getDocumentInEachWorkspace(UUID workspaceId, Integer pageNo, Integer pageSize);

    /*duplicate document*/
    @ResultMap("documentMap")
    @Select("INSERT INTO documents(title, created_date, page_id, workspace_id) SELECT title, created_date, page_id, workspace_id FROM documents WHERE document_id = #{documentId} RETURNING *;")
    Document duplicateDocument(UUID documentId);

    /*Insert TagId And DocumentId Into TagDocument*/
    @Insert("INSERT INTO tag_document(tag_id, document_id) VALUES (#{tagId}, #{documentId});")
    void InsertTagIdAndDocumentIdIntoTagDocument(UUID tagId, UUID documentId);

    /*search document by TagName*/
    @ResultMap("documentMap")
    @Select("SELECT d.document_id, title, status, created_date, page_id, d.workspace_id FROM tags INNER JOIN tag_document td ON tags.tag_id = td.tag_id INNER JOIN documents d ON d.document_id = td.document_id WHERE d.workspace_id = #{workspaceId} AND tag_name = #{tagName};")
    List<Document> searchDocumentByTagName(UUID workspaceId, String tagName);

    /*delete document*/
    @ResultMap("documentMap")
    @Delete("DELETE FROM documents WHERE document_id = #{documentId};")
    void deleteDocument(UUID documentId);

    /*delete TagId and DocumentId In TagDocument*/
    @Delete("DELETE FROM tag_document WHERE document_id = #{documentId};")
    void deleteTagIdAndDocumentIdInTagDocument(UUID documentId);

    /*delete TagId and DocumentId In TagDocument*/
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
            DELETE FROM documents
            WHERE documents.document_id IN (
                SELECT documents.document_id
                FROM documents
                         INNER JOIN user_document ud ON documents.document_id = ud.document_id
                WHERE user_id = #{userId}
            );
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
}

