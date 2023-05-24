package kh.com.kshrd.docengine.services;

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
    Workspace updateWorkspaceName(String name, UUID workspaceId);
    List<Workspace> getAllWorkspace();
    Integer getTotalOfDocument(UUID workspaceId);
    List<Workspace> filterWorkspace(Boolean filter);
    List<Workspace> searchWorkspace(String input);
    void deleteWorkspaceImage(UUID workspaceId);
    Workspace updateWorkspaceImage(String image, UUID workspaceId);
}
