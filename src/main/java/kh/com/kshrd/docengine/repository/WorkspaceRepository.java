package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.model.entity.User;
import kh.com.kshrd.docengine.model.response.MemberResponse;
import kh.com.kshrd.docengine.model.entity.Workspace;
import kh.com.kshrd.docengine.model.request.WorkspaceRequest;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface WorkspaceRepository {
    @Results(id = "workspaceMap", value = {
            @Result(property = "workspaceId",   column = "workspace_id"),
            @Result(property = "workspaceName", column = "workspace_name"),
            @Result(property = "workspaceCode", column = "workspace_code"),
            @Result(property = "createdDate",   column = "created_date"),
            @Result(property = "workspaceImage",column = "workspace_image"),
            @Result(property = "totalDocument", column = "workspace_id", many = @Many(select = "getTotalDocumentOfWorkspace")),
            @Result(property = "createBy", column = "workspace_id", many = @Many(select = "getAllMemberInEachWorkspace"))
    })
    @Select("""
            INSERT INTO workspaces(workspace_name,workspace_image,workspace_code,created_date)
            VALUES(#{w.workspaceName},#{w.workspaceImage},#{workspaceCode},#{localDateTime}) RETURNING *;
            """)
    Workspace createWorkspace(@Param("w") WorkspaceRequest workspaceRequest, String workspaceCode, LocalDateTime localDateTime);

    @ResultMap("workspaceMap")
    @Insert("""
            INSERT INTO user_workspace(user_id,workspace_id,is_owner,accessibility_status)
            VALUES(#{userId},#{workspaceId},true,true)
            """)
    void addUserIdAndWorkspaceIdToUserWorkspaceForOwner(UUID userId, UUID workspaceId);

    @ResultMap("workspaceMap")
    @Select("""
            SELECT * FROM workspaces WHERE workspace_code=#{workspaceCode}
            """)
    Workspace getWorkspaceByCode(String workspaceCode);

    @ResultMap("workspaceMap")
    @Insert("""
            INSERT INTO user_workspace(user_id,workspace_id,is_owner,accessibility_status)
            VALUES(#{userId},#{workspaceId},false,false)
            """)
    void addUserIdAndWorkspaceIdToUserWorkspaceForMember(UUID userId, UUID workspaceId);

    @Delete("""
            DELETE FROM user_workspace WHERE user_id=#{currentUserId} AND workspace_id=#{workspaceId}
            """)
    void leaveWorkspace(UUID currentUserId,UUID workspaceId);

    @Select("SELECT is_owner FROM user_workspace WHERE user_id = #{userIdOfCurrentUser} AND workspace_id = #{workspaceId};")
    Boolean checkIsOwner(UUID userIdOfCurrentUser, UUID workspaceId);

    @Delete("""
            DELETE FROM workspaces WHERE workspace_id=#{workspaceId}
            """)
    void removeWorkspace(UUID workspaceId);

    @Delete("""
            DELETE FROM user_workspace WHERE user_id=#{userId} AND workspace_id=#{workspaceId}
            """)
    void removeMemberFromWorkspace(UUID userId, UUID workspaceId);

    @Update("""
            UPDATE user_workspace SET accessibility_status=#{status}
            WHERE user_id=#{userId} AND workspace_id=#{workspaceId}
            """)
    void setAccessibilityToUser(UUID userId, UUID workspaceId, Boolean status);


    @ResultMap("workspaceMap")
    @Select("""
            SELECT uw.workspace_id,workspace_name,workspace_image,workspace_code,created_date
            FROM workspaces
            INNER JOIN user_workspace uw on workspaces.workspace_id = uw.workspace_id
            WHERE user_id=#{currentUserId} LIMIT #{pageSize} OFFSET #{pageNo};
            """)
    List<Workspace> getAllWorkspaceByUserId(UUID currentUserId, Integer pageNo, Integer pageSize);

    @Select("""
            SELECT count(*) FROM documents inner join workspaces w on documents.workspace_id = w.workspace_id
            WHERE w.workspace_id=#{workspaceId}
            """)
    Integer getTotalDocumentOfWorkspace(UUID workspaceId);

    @ResultMap("workspaceMap")
    @Select("""
            SELECT uw.workspace_id,workspace_name,workspace_image,workspace_code,created_date
            FROM workspaces
            INNER JOIN user_workspace uw on workspaces.workspace_id = uw.workspace_id
            WHERE user_id=#{userIdOfCurrentUser} AND is_owner=#{filter}
            """)
    List<Workspace> filterWorkspace(UUID userIdOfCurrentUser, boolean filter);

    @ResultMap("workspaceMap")
    @Select("""
            SELECT uw.workspace_id,workspace_name,workspace_image,workspace_code,created_date
            FROM workspaces
            INNER JOIN user_workspace uw on workspaces.workspace_id = uw.workspace_id
            WHERE user_id=#{userIdOfCurrentUser} AND workspace_name ILIKE (concat('%', #{workspaceName},'%'))
            """)
    List<Workspace> searchWorkspace(UUID userIdOfCurrentUser,String workspaceName);

    @Update("""
            UPDATE workspaces SET workspace_image=null
            WHERE workspace_id=#{workspaceId}
            """)
    void deleteWorkspaceImage(UUID workspaceId);

    @ResultMap("workspaceMap")
    @Update("""
            UPDATE workspaces SET workspace_name=#{workspaceName}, workspace_image=#{workspaceImage}
            WHERE workspace_id=#{workspaceId};
            """)
    void editWorkspace(UUID workspaceId, String workspaceName, String workspaceImage);

    @ResultMap("workspaceMap")
    @Select("SELECT * FROM workspaces WHERE workspace_id = #{workspaceId};")
    Workspace getWorkspaceByWorkspaceId(UUID workspaceId);

    @Select("SELECT user_id FROM user_workspace WHERE workspace_id = #{workspaceId};")
    List<UUID> getUserIdByWorkspaceId(UUID workspaceId);

    @Results(id = "userWorkspaceMap", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "isOwner", column = "is_owner"),
            @Result(property = "accessibility", column = "accessibility_status")
    })
    @Select("SELECT uw.user_id, username, is_owner, accessibility_status FROM users INNER JOIN user_workspace uw on users.user_id = uw.user_id WHERE workspace_id = #{workspaceId};")
    List<MemberResponse> getAllMemberInEachWorkspace(UUID workspaceId);


    @Select("SELECT accessibility_status FROM user_workspace WHERE user_id = #{userIdOfCurrentUser} AND workspace_id = #{workspaceId};")
    Boolean checkAccessibility(UUID userIdOfCurrentUser, UUID workspaceId);

    @Select("SELECT is_owner FROM user_workspace WHERE workspace_id = #{workspaceId} AND user_id = #{userId};")
    Boolean isOwnerWorkspace(UUID workspaceId, UUID userId);

    @Select("SELECT uw.user_id FROM users INNER JOIN user_workspace uw on users.user_id = uw.user_id WHERE workspace_id = #{workspaceId} AND users.user_id = #{userIdOfCurrentUser};")
    String checkMemberInWorkspace(UUID workspaceId, UUID userIdOfCurrentUser);
}
