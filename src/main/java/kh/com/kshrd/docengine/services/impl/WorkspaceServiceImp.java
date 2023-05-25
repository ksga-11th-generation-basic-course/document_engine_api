package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotOwnerException;
import kh.com.kshrd.docengine.model.entity.Workspace;
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
        if(workspaceRequest.getWorkspaceName() == null){
            throw new BadRequestException("Workspace name cannot be null");
        }else if(workspaceRequest.getWorkspaceName().isBlank()){
            throw new BadRequestException("Workspace name cannot be blank and empty");
        }
        String generatedCode = RandomStringUtils.randomAlphanumeric(10);
        Workspace workspace = workspaceRepository.createWorkspace(workspaceRequest, generatedCode, LocalDateTime.now());
        workspaceRepository.addUserIdAndWorkspaceIdToUserWorkspaceForOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspace.getWorkspaceId());
        return workspace;
    }

    @Override
    public Workspace joinWorkspace(String workspaceCode) {
        if(workspaceCode == null){
            throw new BadRequestException("Workspace code cannot be null");
        } else if (workspaceCode.isBlank()) {
            throw new BadRequestException("Workspace code cannot be blank or empty");
        }
        Workspace workspace = workspaceRepository.getWorkspaceByCode(workspaceCode);
        if (Objects.equals(workspaceCode, workspace.getWorkspaceCode())) {
            workspaceRepository.addUserIdAndWorkspaceIdToUserWorkspaceForMember(userAuthenticationService.getUserIdOfCurrentUser(), workspace.getWorkspaceId());
        } else {
            throw new BadRequestException("WorkspaceCode is incorrect");
        }
        return workspace;
    }

    @Override
    public void leaveWorkspace(UUID workspaceId) {
        if(workspaceId == null){
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        workspaceRepository.leaveWorkspace(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId);
    }

    @Override
    public void removeWorkspace(UUID workspaceId) {
        if(workspaceId == null){
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        if (workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId)) {
            workspaceRepository.removeWorkspace(workspaceId);
        } else {
            throw new NotOwnerException("You are not the owner of this workspace");
        }
    }

    @Override
    public void removeMemberFromWorkspace(UUID userId, UUID workspaceId) {
        exceptionUserIdAndWorkspaceId(userId, workspaceId);
        if (workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId)) {
            workspaceRepository.removeMemberFromWorkspace(userId);
        } else {
            throw new NotOwnerException("You are not the owner of this workspace");
        }
    }

    @Override
    public void setAccessibilityToUser(UUID userId, UUID workspaceId, Boolean status) {
        exceptionUserIdAndWorkspaceId(userId, workspaceId);
        if (workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId)) {
            workspaceRepository.setAccessibilityToUser(userId, workspaceId, status);
        } else {
            throw new NotOwnerException("You are not the owner of this workspace");
        }
    }

    private void exceptionUserIdAndWorkspaceId(UUID userId, UUID workspaceId) {
        if(workspaceId == null){
            throw new BadRequestException("Workspace id cannot be null");
        } else if (userId == null) {
            throw new BadRequestException("User id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        } else if (userId.toString().isBlank()) {
            throw new BadRequestException("User id cannot be blank or empty");
        }
    }

    @Override
    public Workspace updateWorkspaceName(String name, UUID workspaceId) {
        if(name == null){
            throw new BadRequestException("name id cannot be null");
        } else if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (name.isBlank()) {
            throw new BadRequestException("Name id cannot be blank or empty");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        if (workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId)) {
            workspaceRepository.updateWorkspaceNameById(name, workspaceId);
        } else {
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
        if(workspaceId == null){
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        return workspaceRepository.getTotalDocumentOfWorkspace(workspaceId);
    }

    @Override
    public List<Workspace> filterWorkspace(Boolean filter) {
        List<Workspace> workspace = null;
        if (!filter) {
            workspace = workspaceRepository.filterWorkspace(userAuthenticationService.getUserIdOfCurrentUser(), false);
        }
        if (filter) {
            workspace = workspaceRepository.filterWorkspace(userAuthenticationService.getUserIdOfCurrentUser(), true);
        }
        return workspace;
    }

    @Override
    public List<Workspace> searchWorkspace(String search) {
        if(search == null){
            throw new BadRequestException("Input id cannot be null");
        } else if (search.isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        return workspaceRepository.searchWorkspace(userAuthenticationService.getUserIdOfCurrentUser(), search);
    }

    @Override
    public void deleteWorkspaceImage(UUID workspaceId) {
        if(workspaceId == null){
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        if (workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId)) {
            workspaceRepository.deleteWorkspaceImage(workspaceId, null);
        }
    }

    @Override
    public Workspace updateWorkspaceImage(String image, UUID workspaceId) {
        if(workspaceId == null){
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        if (workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId)) {
            workspaceRepository.updateWorkspaceImageById(image, workspaceId);
        } else {
            throw new NotOwnerException("You are not the owner of this workspace");
        }
        return workspaceRepository.getWorkspaceById(workspaceId);
    }


}
