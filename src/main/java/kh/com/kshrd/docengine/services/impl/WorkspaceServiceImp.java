package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotOwnerException;
import kh.com.kshrd.docengine.model.Workspace;
import kh.com.kshrd.docengine.model.request.WorkspaceRequest;
import kh.com.kshrd.docengine.repository.WorkspaceRepository;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import kh.com.kshrd.docengine.services.WorkspaceService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WorkspaceServiceImp implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final UserAuthenticationService userAuthenticationService;
    @Override
    public Workspace createWorkspace(WorkspaceRequest workspaceRequest) {
        String generatedCode  =  RandomStringUtils.randomAlphanumeric(10);
        Workspace workspace   =  workspaceRepository.createWorkspace(workspaceRequest,generatedCode, LocalDateTime.now());
        workspaceRepository.addUserIdAndWorkspaceIdToUserWorkspaceForOwner(userAuthenticationService.getUserIdOfCurrentUser(),workspace.getWorkspaceId());
        return workspace;
    }

    @Override
    public Workspace joinWorkspace(String workspaceCode) {
        Workspace workspace = workspaceRepository.getWorkspaceByCode(workspaceCode);
        if(Objects.equals(workspaceCode, workspace.getWorkspaceCode())){
            workspaceRepository.addUserIdAndWorkspaceIdToUserWorkspaceForMember(userAuthenticationService.getUserIdOfCurrentUser(),workspace.getWorkspaceId());
        }else{
            throw new BadRequestException("WorkspaceCode is incorrect");
        }
        return workspace;
    }

    @Override
    public void leaveWorkspace(UUID workspaceId) {
        workspaceRepository.leaveWorkspace(userAuthenticationService.getUserIdOfCurrentUser(),workspaceId);
    }

    @Override
    public void removeWorkspace(UUID workspaceId) {
        if(workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(),workspaceId)){
            workspaceRepository.removeWorkspace(workspaceId);
        }else{
            throw new NotOwnerException("You are not the owner of this workspace");
        }
    }

    @Override
    public void removeMemberFromWorkspace(UUID userId,UUID workspaceId) {
        if(workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(),workspaceId)){
            workspaceRepository.removeMemberFromWorkspace(userId);
        }else{
            throw new NotOwnerException("You are not the owner of this workspace");
        }
    }

    @Override
    public void setAccessibilityToUser(UUID userId, UUID workspaceId,Boolean status) {
        if(workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(),workspaceId)){
            workspaceRepository.setAccessibilityToUser(userId,workspaceId,status);
        }else{
            throw new NotOwnerException("You are not the owner of this workspace");
        }
    }

    @Override
    public Workspace updateWorkspaceName(String name, UUID workspaceId) {
        if(workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(),workspaceId)){
           workspaceRepository.updateWorkspaceNameById(name,workspaceId);
        }else{
            throw new NotOwnerException("You are not the owner of this workspace");
        }
        return workspaceRepository.getWorkspaceById(workspaceId);
    }

    @Override
    public List<Workspace> getAllWorkspace() {
        return workspaceRepository.getAllWorkspaceByUserId(userAuthenticationService.getUserIdOfCurrentUser());
    }

    @Override
    public Integer getTotalOfDocument(UUID workspaceId) {
        return workspaceRepository.getTotalDocumentOfWorkspace(workspaceId);
    }

    @Override
    public List<Workspace> filterWorkspace(Boolean filter) {
        List<Workspace> workspace = null;
        if(!filter){
            workspace=workspaceRepository.filterWorkspace(userAuthenticationService.getUserIdOfCurrentUser(),false);
        }
        if(filter){
            workspace=workspaceRepository.filterWorkspace(userAuthenticationService.getUserIdOfCurrentUser(),true);
        }
        return workspace;
    }

    @Override
    public List<Workspace> searchWorkspace(String input) {
        return workspaceRepository.searchWorkspace(userAuthenticationService.getUserIdOfCurrentUser(),input);
    }

    @Override
    public void deleteWorkspaceImage(UUID workspaceId) {
        if(workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(),workspaceId)){
            workspaceRepository.deleteWorkspaceImage(workspaceId,null);
        }
    }

    @Override
    public Workspace updateWorkspaceImage(String image, UUID workspaceId) {
        if(workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(),workspaceId)){
            workspaceRepository.updateWorkspaceImageById(image,workspaceId);
        }else{
            throw new NotOwnerException("You are not the owner of this workspace");
        }
        return workspaceRepository.getWorkspaceById(workspaceId);
    }


}
