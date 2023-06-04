package kh.com.kshrd.docengine.services.impl;

import kh.com.kshrd.docengine.exceptions.BadRequestException;
import kh.com.kshrd.docengine.exceptions.NotDuplicateException;
import kh.com.kshrd.docengine.exceptions.NotFoundException;
import kh.com.kshrd.docengine.exceptions.NotOwnerException;
import kh.com.kshrd.docengine.model.entity.Document;
import kh.com.kshrd.docengine.model.entity.User;
import kh.com.kshrd.docengine.model.response.MemberResponse;
import kh.com.kshrd.docengine.model.entity.Workspace;
import kh.com.kshrd.docengine.model.request.WorkspaceRequest;
import kh.com.kshrd.docengine.repository.DocumentRepository;
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
    private final DocumentRepository documentRepository;

    @Override
    public Workspace createWorkspace(WorkspaceRequest workspaceRequest) {
        if (workspaceRequest.getWorkspaceName() == null) {
            throw new BadRequestException("Workspace name cannot be null");
        } else if (workspaceRequest.getWorkspaceName().isBlank()) {
            throw new BadRequestException("Workspace name cannot be blank and empty");
        } else if (workspaceRequest.getWorkspaceImage().isBlank()) {
            throw new BadRequestException("Workspace image cannot be blank and empty");
        }
        String generatedCode = RandomStringUtils.randomAlphanumeric(10);
        Workspace workspace = workspaceRepository.createWorkspace(workspaceRequest, generatedCode, LocalDateTime.now());
        workspaceRepository.addUserIdAndWorkspaceIdToUserWorkspaceForOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspace.getWorkspaceId());
        return workspace;
    }

    @Override
    public Workspace joinWorkspace(String workspaceCode) {
        if (workspaceCode == null) {
            throw new BadRequestException("Workspace code cannot be null");
        } else if (workspaceCode.isBlank()) {
            throw new BadRequestException("Workspace code cannot be blank or empty");
        }


        Workspace workspace = workspaceRepository.getWorkspaceByCode(workspaceCode);
        if (workspace == null) {
            throw new NotFoundException("Workspace doesn't exist");
        }

        if (Objects.equals(workspaceCode, workspace.getWorkspaceCode())) {
            List<UUID> usersId = workspaceRepository.getUserIdByWorkspaceId(workspace.getWorkspaceId());
            for (UUID userId : usersId) {
                if (userId.equals(userAuthenticationService.getUserIdOfCurrentUser())) {
                    throw new NotDuplicateException("You already join this workspace");
                }
            }
            workspaceRepository.addUserIdAndWorkspaceIdToUserWorkspaceForMember(userAuthenticationService.getUserIdOfCurrentUser(), workspace.getWorkspaceId());
            List<Document> documents = documentRepository.getDocumentByWorkspaceId(workspace.getWorkspaceId());
            for (Document document : documents) {
                documentRepository.addUserIdDocumentIdToUserDocument(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId(), "NO_ACCESS");
            }
        } else {
            throw new BadRequestException("WorkspaceCode is incorrect");
        }
        return workspace;
    }

    @Override
    public void leaveWorkspace(UUID workspaceId) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }

        String checkMemberInWorkspace = workspaceRepository.checkMemberInWorkspace(workspaceId, userAuthenticationService.getUserIdOfCurrentUser());

        if (checkMemberInWorkspace != null) {
            Workspace workspace = workspaceRepository.getWorkspaceByWorkspaceId(workspaceId);
            if (workspace == null) {
                throw new NotFoundException("Workspace doesn't exist");
            } else {
                if (workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId)) {
                    throw new BadRequestException("You are owner you cannot leave this workspace");
                } else {
                    workspaceRepository.leaveWorkspace(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId);
                }
            }
        } else {
            throw new NotFoundException("You are not member in workspace");
        }


    }

    @Override
    public void removeWorkspace(UUID workspaceId) {
        validateWorkspaceId(workspaceId);

        Workspace workspace = workspaceRepository.getWorkspaceByWorkspaceId(workspaceId);
        if (workspace == null) {
            throw new NotFoundException("Workspace doesn't exist");
        } else {
            if (workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId)) {
                workspaceRepository.removeWorkspace(workspaceId);
            } else {
                throw new NotOwnerException("You are not the owner of this workspace");
            }
        }
    }

    @Override
    public void removeMemberFromWorkspace(UUID userId, UUID workspaceId) {
        exceptionUserIdAndWorkspaceId(userId, workspaceId);

        Boolean isWorkspaceOwner = workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId);

        if (isWorkspaceOwner == null) {
            throw new NotFoundException("You are not member in workspace");
        }

        Workspace workspace = workspaceRepository.getWorkspaceByWorkspaceId(workspaceId);
        if (workspace == null) {
            throw new NotFoundException("Workspace doesn't exist");
        } else {
            if (isWorkspaceOwner) {
                if (workspaceRepository.checkIsOwner(userId, workspaceId)) {
                    throw new BadRequestException("You are owner you cannot remove yourself");
                }
                workspaceRepository.removeMemberFromWorkspace(userId, workspaceId);
                documentRepository.deleteDocumentFromUserDocument(userId);
            } else {
                throw new NotOwnerException("You are not the owner of this workspace");
            }
        }
    }

    @Override
    public void setAccessibilityToUser(UUID userId, UUID workspaceId, Boolean status) {
        exceptionUserIdAndWorkspaceId(userId, workspaceId);
        if (workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId) != null) {
            workspaceRepository.setAccessibilityToUser(userId, workspaceId, status);
        } else {
            throw new NotOwnerException("You are not the owner of this workspace");
        }
    }

    private void exceptionUserIdAndWorkspaceId(UUID userId, UUID workspaceId) {
        if (workspaceId == null) {
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
    public List<Workspace> getAllWorkspace(Integer pageNo, Integer pageSize) {
        pageNo = (pageNo - 1) * pageSize;
        return workspaceRepository.getAllWorkspaceByUserId(userAuthenticationService.getUserIdOfCurrentUser(), pageNo, pageSize);
    }

    @Override
    public Integer getTotalOfDocument(UUID workspaceId) {
        if (workspaceId == null) {
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
    public List<Workspace> searchWorkspace(String workspaceName) {
        if (workspaceName == null) {
            throw new BadRequestException("Search cannot be null");
        } else if (workspaceName.isBlank()) {
            throw new BadRequestException("Search cannot be blank or empty");
        }
        return workspaceRepository.searchWorkspace(userAuthenticationService.getUserIdOfCurrentUser(), workspaceName);
    }

    @Override
    public void deleteWorkspaceImage(UUID workspaceId) {
        validateWorkspaceId(workspaceId);

        Workspace workspace = workspaceRepository.getWorkspaceByWorkspaceId((workspaceId));
        if (workspace == null) {
            throw new NotFoundException("Workspace doesn't exist");
        } else {
            if (workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId)) {
                workspaceRepository.deleteWorkspaceImage(workspaceId);
            } else {
                throw new NotOwnerException("You are not the owner of this workspace");
            }
        }
    }

    private void validateWorkspaceId(UUID workspaceId) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }

        Boolean isWorkspaceOwner = workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId);

        if (isWorkspaceOwner == null) {
            throw new NotFoundException("You are not member in workspace");
        }
    }

    @Override
    public void editWorkspace(UUID workspaceId, String workspaceName, String workspaceImage) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        Boolean isOwner = workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId);
        if (isOwner == null) {
            throw new NotOwnerException("You are not the owner of this workspace");
        } else if (isOwner) {
            Workspace workspace = workspaceRepository.getWorkspaceByWorkspaceId(workspaceId);
            if (workspace == null) {
                throw new NotFoundException("Workspace doesn't exist");
            }
            if (workspaceName == null || workspaceName.isBlank()) {
                workspaceRepository.editWorkspace(workspaceId, workspace.getWorkspaceName(), workspaceImage);
            } else if (workspaceImage == null || workspaceImage.isBlank()) {
                workspaceRepository.editWorkspace(workspaceId, workspaceName, workspace.getWorkspaceImage());
            } else {
                workspaceRepository.editWorkspace(workspaceId, workspaceName, workspaceImage);
            }
        } else {
            throw new NotOwnerException("You are not the owner of this workspace");
        }
    }

    @Override
    public Workspace getWorkspaceByWorkspaceId(UUID workspaceId) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        Workspace workspace = workspaceRepository.getWorkspaceByWorkspaceId(workspaceId);
        if (workspace == null) {
            throw new NotFoundException("Workspace doesn't exist");
        }
        return workspace;
    }

    @Override
    public List<MemberResponse> getAllMemberInEachWorkspace(UUID workspaceId) {
        return workspaceRepository.getAllMemberInEachWorkspace(workspaceId);
    }


}
