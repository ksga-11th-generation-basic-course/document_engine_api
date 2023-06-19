package kh.com.kshrd.docengine.services;

import jakarta.mail.MessagingException;
import kh.com.kshrd.docengine.enums.ESortCurrentDateTime;
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
    MemberResponse setAccessibilityToUser(UUID userId, UUID workspaceId,Boolean status);
    List<Workspace> getAllWorkspace(Integer pageNo, Integer pageSize, Boolean asc, Boolean desc, ESortCurrentDateTime eSortWorkspace);
    Integer getTotalOfDocument(UUID workspaceId);
    List<Workspace> filterWorkspace(Boolean filter, Integer pageNo, Integer pageSize, Boolean asc, Boolean desc, ESortCurrentDateTime eSortWorkspace);
    List<Workspace> searchWorkspace(String workspaceName);
    Workspace deleteWorkspaceImage(UUID workspaceId);
    void editWorkspace(UUID workspaceId, WorkspaceRequest workspaceRequest);
    Workspace getWorkspaceByWorkspaceId(UUID workspaceId);
    List<MemberResponse> getAllMemberInEachWorkspace(UUID workspaceId);
    Workspace getWorkspaceById(UUID workspaceId);
    Workspace inviteMemberByEmail(UUID workspaceId, String email) throws MessagingException;
    Boolean checkIsOwnerWorkspace(UUID workspaceId, UUID userId);
    Boolean checkAccessibility(UUID workspaceId);

    Integer getTotalPage(Integer pageSize);
}
