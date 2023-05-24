package kh.com.kshrd.docengine.repository;

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
            DELETE FROM user_workspace WHERE user_id=#{userId}
            """)
    void removeMemberFromWorkspace(UUID userId);

    @Update("""
            UPDATE user_workspace SET accessibility_status=#{status}
            WHERE user_id=#{userId} AND workspace_id=#{workspaceId}
            """)
    void setAccessibilityToUser(UUID userId, UUID workspaceId, Boolean status);

    @Update("""
            UPDATE workspaces SET workspace_name=#{name}
            WHERE workspace_id=#{workspaceId}
            """)
    void updateWorkspaceNameById(String name,UUID workspaceId);

    @ResultMap("workspaceMap")
    @Select("""
            SELECT * FROM workspaces WHERE workspace_id=#{workspaceId}
            """)
    Workspace getWorkspaceById(UUID workspaceId);


    @ResultMap("workspaceMap")
    @Select("""
            SELECT uw.workspace_id,workspace_name,workspace_image,workspace_code,created_date
            FROM workspaces
            INNER JOIN user_workspace uw on workspaces.workspace_id = uw.workspace_id
            WHERE user_id=#{currentUserId}
            """)
    List<Workspace> getAllWorkspaceByUserId(UUID currentUserId);

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
            WHERE user_id=#{userIdOfCurrentUser} AND workspace_name ILIKE (concat('%', #{input},'%'))
            """)
    List<Workspace> searchWorkspace(UUID userIdOfCurrentUser,String input);

    @Update("""
            UPDATE workspaces SET workspace_image=#{image}
            WHERE workspace_id=#{workspaceId}
            """)
    void deleteWorkspaceImage(UUID workspaceId,String image);

    @Update("""
            UPDATE workspaces SET workspace_image=#{image}
            WHERE workspace_id=#{workspaceId}
            """)
    void updateWorkspaceImageById(String image, UUID workspaceId);
}
