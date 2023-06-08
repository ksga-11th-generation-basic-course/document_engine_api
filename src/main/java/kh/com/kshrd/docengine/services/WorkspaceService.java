package kh.com.kshrd.docengine.services;

import kh.com.kshrd.docengine.model.response.MemberResponse;
import kh.com.kshrd.docengine.model.entity.Workspace;
import kh.com.kshrd.docengine.model.request.WorkspaceRequest;

import java.util.List;
import java.util.UUID;

public interface WorkspaceService {
    Workspace createWorkspace(WorkspaceRequest workspaceRequest);
    Workspace joinWorkspace(String workspaceCode);
    void leaveWorkspace(UUID workspaceId);
    void removeWorkspace(UUID workspaceId);
    void removeMemberFromWorkspace(UUID userId,UUID workspaceId);
    void setAccessibilityToUser(UUID userId, UUID workspaceId,Boolean status);
    List<Workspace> getAllWorkspace(Integer pageNo, Integer pageSize);
    Integer getTotalOfDocument(UUID workspaceId);
    List<Workspace> filterWorkspace(Boolean filter);
    List<Workspace> searchWorkspace(String workspaceName);
    void deleteWorkspaceImage(UUID workspaceId);
    void editWorkspace(UUID workspaceId, WorkspaceRequest workspaceRequest);
    Workspace getWorkspaceByWorkspaceId(UUID workspaceId);
    List<MemberResponse> getAllMemberInEachWorkspace(UUID workspaceId);

    Workspace getWorkspaceById(UUID workspaceId);
}
