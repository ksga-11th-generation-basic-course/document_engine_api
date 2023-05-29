package kh.com.kshrd.docengine.repository;

import kh.com.kshrd.docengine.model.entity.Workspace;
import kh.com.kshrd.docengine.model.request.WorkspaceRequest;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface WorkspaceRepository {

  /*  create workspace*/
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

    /*add UserId And WorkspaceId To User Workspace For Owner*/
    @ResultMap("workspaceMap")
    @Insert("""
            INSERT INTO user_workspace(user_id,workspace_id,is_owner,accessibility_status)
            VALUES(#{userId},#{workspaceId},true,true)
            """)
    void addUserIdAndWorkspaceIdToUserWorkspaceForOwner(UUID userId, UUID workspaceId);

   /* get workspace by code*/
    @ResultMap("workspaceMap")
    @Select("""
            SELECT * FROM workspaces WHERE workspace_code=#{workspaceCode}
            """)
    Workspace getWorkspaceByCode(String workspaceCode);

    /*add UserId And WorkspaceId ToUser Workspace For Member*/
    @ResultMap("workspaceMap")
    @Insert("""
            INSERT INTO user_workspace(user_id,workspace_id,is_owner,accessibility_status)
            VALUES(#{userId},#{workspaceId},false,false)
            """)
    void addUserIdAndWorkspaceIdToUserWorkspaceForMember(UUID userId, UUID workspaceId);

    /*leave workspace*/
    @Delete("""
            DELETE FROM user_workspace WHERE user_id=#{currentUserId} AND workspace_id=#{workspaceId}
            """)
    void leaveWorkspace(UUID currentUserId,UUID workspaceId);

    /*check IsOwner*/
    @Select("SELECT is_owner FROM user_workspace WHERE user_id = #{userIdOfCurrentUser} AND workspace_id = #{workspaceId};")
    Boolean checkIsOwner(UUID userIdOfCurrentUser, UUID workspaceId);

  /*  remove workspace*/
    @Delete("""
            DELETE FROM workspaces WHERE workspace_id=#{workspaceId}
            """)
    void removeWorkspace(UUID workspaceId);

   /* remove member from workspace*/
    @Delete("""
            DELETE FROM user_workspace WHERE user_id=#{userId} AND workspace_id=#{workspaceId}
            """)
    void removeMemberFromWorkspace(UUID userId, UUID workspaceId);


   /* set accessibility to user*/
    @Update("""
            UPDATE user_workspace SET accessibility_status=#{status}
            WHERE user_id=#{userId} AND workspace_id=#{workspaceId}
            """)
    void setAccessibilityToUser(UUID userId, UUID workspaceId, Boolean status);


 /*   getAllWorkspace By UserId*/
    @ResultMap("workspaceMap")
    @Select("""
            SELECT uw.workspace_id,workspace_name,workspace_image,workspace_code,created_date
            FROM workspaces
            INNER JOIN user_workspace uw on workspaces.workspace_id = uw.workspace_id
            WHERE user_id=#{currentUserId}
            """)
    List<Workspace> getAllWorkspaceByUserId(UUID currentUserId);

   /* get total document of workspace*/
    @Select("""
            SELECT count(*) FROM documents inner join workspaces w on documents.workspace_id = w.workspace_id
            WHERE w.workspace_id=#{workspaceId}
            """)
    Integer getTotalDocumentOfWorkspace(UUID workspaceId);

    /*filter workspace*/
    @ResultMap("workspaceMap")
    @Select("""
            SELECT uw.workspace_id,workspace_name,workspace_image,workspace_code,created_date
            FROM workspaces
            INNER JOIN user_workspace uw on workspaces.workspace_id = uw.workspace_id
            WHERE user_id=#{userIdOfCurrentUser} AND is_owner=#{filter}
            """)
    List<Workspace> filterWorkspace(UUID userIdOfCurrentUser, boolean filter);

   /* search workspace*/
    @ResultMap("workspaceMap")
    @Select("""
            SELECT uw.workspace_id,workspace_name,workspace_image,workspace_code,created_date
            FROM workspaces
            INNER JOIN user_workspace uw on workspaces.workspace_id = uw.workspace_id
            WHERE user_id=#{userIdOfCurrentUser} AND workspace_name ILIKE (concat('%', #{workspaceName},'%'))
            """)
    List<Workspace> searchWorkspace(UUID userIdOfCurrentUser,String workspaceName);

  /*  delete workspace image*/
    @Update("""
            UPDATE workspaces SET workspace_image=null
            WHERE workspace_id=#{workspaceId}
            """)
    void deleteWorkspaceImage(UUID workspaceId);

  /*  edit workspace*/
    @ResultMap("workspaceMap")
    @Update("""
            UPDATE workspaces SET workspace_name=#{workspaceName}, workspace_image=#{workspaceImage}
            WHERE workspace_id=#{workspaceId};
            """)
    void editWorkspace(UUID workspaceId, String workspaceName, String workspaceImage);

   /* get Workspace By WorkspaceId*/
    @ResultMap("workspaceMap")
    @Select("SELECT * FROM workspaces WHERE workspace_id = #{workspaceId};")
    Workspace getWorkspaceByWorkspaceId(UUID workspaceId);

  /*  get UserId By WorkspaceId*/
    @Select("SELECT user_id FROM user_workspace WHERE workspace_id = #{workspaceId};")
    List<UUID> getUserIdByWorkspaceId(UUID workspaceId);
}
