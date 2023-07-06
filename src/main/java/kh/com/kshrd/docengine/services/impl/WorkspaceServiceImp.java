package kh.com.kshrd.docengine.services.impl;

import jakarta.mail.MessagingException;
import kh.com.kshrd.docengine.enums.ESortCurrentDateTime;
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
import kh.com.kshrd.docengine.repository.UserRepository;
import kh.com.kshrd.docengine.repository.WorkspaceRepository;
import kh.com.kshrd.docengine.security.model.entity.UserAuthentication;
import kh.com.kshrd.docengine.security.services.EmailService;
import kh.com.kshrd.docengine.security.services.UserAuthenticationService;
import kh.com.kshrd.docengine.services.WorkspaceService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WorkspaceServiceImp implements WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final UserAuthenticationService userAuthenticationService;
    private final DocumentRepository documentRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    final String pattern = "^[A-Za-z_][A-Za-z0-9_\\s]{0,39}$";

    @Override
    public Workspace createWorkspace(WorkspaceRequest workspaceRequest) {
        if (workspaceRequest.getWorkspaceName() == null) {
            throw new BadRequestException("Workspace name cannot be null");
        } else if (workspaceRequest.getWorkspaceImage() == null) {
            throw new BadRequestException("Workspace image cannot be null");
        } else if (workspaceRequest.getWorkspaceName().isBlank()) {
            throw new BadRequestException("Workspace name cannot be blank and empty");
        } else if (workspaceRequest.getWorkspaceImage().isBlank()) {
            throw new BadRequestException("Workspace image cannot be blank and empty");
        } else if (!workspaceRequest.getWorkspaceName().matches(pattern)) {
            throw new BadRequestException("Workspace name must be less than 40 character");
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
                documentRepository.addUserIdDocumentIdToUserDocument(userAuthenticationService.getUserIdOfCurrentUser(), document.getDocumentId(), "VIEWER");
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
            Workspace workspace = workspaceRepository.getWorkspaceById(workspaceId);
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

        Workspace workspace = workspaceRepository.getWorkspaceById(workspaceId);
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

        Workspace workspace = workspaceRepository.getWorkspaceById(workspaceId);
        if (workspace == null) {
            throw new NotFoundException("Workspace doesn't exist");
        } else {
            if (isWorkspaceOwner) {
                if (workspaceRepository.checkIsOwner(userId, workspaceId) == null) {
                    throw new NotFoundException("This user is not a member in this workspace");
                } else if (workspaceRepository.checkIsOwner(userId, workspaceId)) {
                    throw new BadRequestException("You are owner you cannot remove yourself");
                }
                workspaceRepository.removeMemberFromWorkspace(userId, workspaceId);
                documentRepository.deleteDocumentFromUserDocument(userId);
                List<Document> documents = documentRepository.getAllDocumentByWorkspaceId(workspaceId);
                for(Document document : documents) {
                    documentRepository.removeMemberFromDocument(document.getDocumentId(), userId);
                }
            } else {
                throw new NotOwnerException("You are not the owner of this workspace");
            }
        }
    }

    @Override
    public MemberResponse setAccessibilityToUser(UUID userId, UUID workspaceId, Boolean status) {
        exceptionUserIdAndWorkspaceId(userId, workspaceId);

        Boolean checkIsUserInWorkspace = workspaceRepository.checkIsUserInWorkspace(userId, workspaceId);
        if (!checkIsUserInWorkspace) {
            throw new NotFoundException("User is not in this workspace");
        }

        Boolean checkIsOwner = workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId);
        if (checkIsOwner == null || !checkIsOwner) {
            throw new NotOwnerException("You are not the owner of this workspace");
        }
        return workspaceRepository.setAccessibilityToUser(userId, workspaceId, status);
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
    public List<Workspace> getAllWorkspace(Integer pageNo, Integer pageSize, Boolean asc, Boolean desc, ESortCurrentDateTime eSortWorkspace) {
        pageNo = (pageNo - 1) * pageSize;

        List<Workspace> workspaces = workspaceRepository.getAllWorkspaceByUserId(userAuthenticationService.getUserIdOfCurrentUser(), pageNo, pageSize, asc, desc);

        boolean isTrue = false;
        for (ESortCurrentDateTime sortWorkspace : ESortCurrentDateTime.values()) {
            if (eSortWorkspace.toString().equalsIgnoreCase(sortWorkspace.name())) {
                isTrue = true;
                break;
            }
        }
        if (!isTrue) {
            throw new BadRequestException("This sort by week, month and year are not correct : 'String' , " +
                    "please input one of (THIS_WEEK, THIS_MONTH and THIS_YEAR)");
        } else if (eSortWorkspace.toString().isBlank()) {
            throw new BadRequestException("This field could not empty");
        }

        switch (eSortWorkspace) {
            case THIS_WEEK -> {
                LocalDate now = LocalDate.now();
                LocalDate startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
                LocalDate endOfWeek = now.with(java.time.DayOfWeek.SUNDAY);

                return workspaces.stream()
                        .filter(workspace -> {
                            LocalDate workspaceDate = workspace.getCreatedDate().toLocalDate();
                            return !workspaceDate.isBefore(startOfWeek) && !workspaceDate.isAfter(endOfWeek);
                        })
                        .collect(Collectors.toList());
            }
            case THIS_MONTH -> {
                YearMonth currentMonth = YearMonth.now();

                return workspaces.stream()
                        .filter(workspace -> {
                            YearMonth workspaceMonth = YearMonth.from(workspace.getCreatedDate());
                            return workspaceMonth.equals(currentMonth);
                        })
                        .collect(Collectors.toList());
            }
            case THIS_YEAR -> {
                Year currentYear = Year.now();

                return workspaces.stream()
                        .filter(workspace -> {
                            Year workspaceYear = Year.of(workspace.getCreatedDate().getYear());
                            return workspaceYear.equals(currentYear);
                        })
                        .collect(Collectors.toList());
            }
            default -> {
                return workspaces;
            }
        }
    }

    @Override
    public Integer getTotalOfDocument(UUID workspaceId) {
        validateWorkspaceIdAndNotFoundWorkspace(workspaceId);
        return workspaceRepository.getTotalDocumentOfWorkspace(workspaceId);
    }

    private void validateWorkspaceIdAndNotFoundWorkspace(UUID workspaceId) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        Workspace workspace = workspaceRepository.getWorkspaceById(workspaceId);
        if (workspace == null) {
            throw new NotFoundException("Workspace doesn't exist");
        }
    }

    @Override
    public List<Workspace> filterWorkspace(Boolean filter, Integer pageNo, Integer pageSize, Boolean asc, Boolean desc, ESortCurrentDateTime eSortWorkspace) {
        pageNo = (pageNo - 1) * pageSize;

        List<Workspace> workspaces = workspaceRepository.filterWorkspace(userAuthenticationService.getUserIdOfCurrentUser(), filter, pageNo, pageSize, asc, desc);

        boolean isTrue = false;
        for (ESortCurrentDateTime sortWorkspace : ESortCurrentDateTime.values()) {
            if (eSortWorkspace.toString().equalsIgnoreCase(sortWorkspace.name())) {
                isTrue = true;
                break;
            }
        }
        if (!isTrue) {
            throw new BadRequestException("This sort by week, month and year are not correct : 'String' , " +
                    "please input one of (THIS_WEEK, THIS_MONTH and THIS_YEAR)");
        } else if (eSortWorkspace.toString().isBlank()) {
            throw new BadRequestException("This field could not empty");
        }

        switch (eSortWorkspace) {
            case THIS_WEEK -> {
                LocalDate now = LocalDate.now();
                LocalDate startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
                LocalDate endOfWeek = now.with(java.time.DayOfWeek.SUNDAY);

                return workspaces.stream()
                        .filter(workspace -> {
                            LocalDate workspaceDate = workspace.getCreatedDate().toLocalDate();
                            return !workspaceDate.isBefore(startOfWeek) && !workspaceDate.isAfter(endOfWeek);
                        })
                        .collect(Collectors.toList());
            }
            case THIS_MONTH -> {
                YearMonth currentMonth = YearMonth.now();

                return workspaces.stream()
                        .filter(workspace -> {
                            YearMonth workspaceMonth = YearMonth.from(workspace.getCreatedDate());
                            return workspaceMonth.equals(currentMonth);
                        })
                        .collect(Collectors.toList());
            }
            case THIS_YEAR -> {
                Year currentYear = Year.now();

                return workspaces.stream()
                        .filter(workspace -> {
                            Year workspaceYear = Year.of(workspace.getCreatedDate().getYear());
                            return workspaceYear.equals(currentYear);
                        })
                        .collect(Collectors.toList());
            }
            default -> {
                return workspaces;
            }
        }
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
    public Workspace deleteWorkspaceImage(UUID workspaceId) {
        validateWorkspaceId(workspaceId);

        Workspace workspace = workspaceRepository.getWorkspaceById((workspaceId));
        if (workspace == null) {
            throw new NotFoundException("Workspace doesn't exist");
        } else {
            if (workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId)) {
                return workspaceRepository.deleteWorkspaceImage(workspaceId);
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
    }

    @Override
    public void editWorkspace(UUID workspaceId, WorkspaceRequest workspaceRequest) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        } else if (!workspaceRequest.getWorkspaceName().matches(pattern)) {
            throw new BadRequestException("Workspace name must be less than 30 character");
        }
        Boolean isOwner = workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId);
        if (isOwner == null) {
            throw new NotOwnerException("You are not the owner of this workspace");
        } else if (isOwner) {
            Workspace workspace = workspaceRepository.getWorkspaceById(workspaceId);
            if (workspace == null) {
                throw new NotFoundException("Workspace doesn't exist");
            }
            if (workspaceRequest.getWorkspaceName() == null || workspaceRequest.getWorkspaceName().isBlank()) {
                workspaceRepository.editWorkspace(workspaceId, workspace.getWorkspaceName(), workspaceRequest.getWorkspaceImage());
            } else if (workspaceRequest.getWorkspaceImage() == null || workspaceRequest.getWorkspaceImage().isBlank()) {
                workspaceRepository.editWorkspace(workspaceId, workspaceRequest.getWorkspaceName(), workspace.getWorkspaceImage());
            } else {
                workspaceRepository.editWorkspace(workspaceId, workspaceRequest.getWorkspaceName(), workspaceRequest.getWorkspaceImage());
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

    @Override
    public Workspace getWorkspaceById(UUID workspaceId) {
        validateWorkspaceIdAndNotFoundWorkspace(workspaceId);
        return workspaceRepository.getWorkspaceById(workspaceId);
    }

    @Override
    public Workspace inviteMemberByEmail(UUID workspaceId, String email) throws MessagingException {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (email == null) {
            throw new BadRequestException("Email cannot be null");
        } else if (email.isBlank()) {
            throw new BadRequestException("Email cannot be blank or empty");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }

        Boolean isWorkspaceOwner = workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId);

        if (isWorkspaceOwner == null) {
            throw new NotFoundException("You are not member in workspace");
        }

        Workspace workspace = workspaceRepository.getWorkspaceById(workspaceId);
        if (workspace == null) {
            throw new NotFoundException("Workspace doesn't exist");
        } else {
            if (isWorkspaceOwner) {
                UserAuthentication userAuthentication = userAuthenticationService.getByEmail(email);
                if(userAuthentication == null) {
                    throw new NotFoundException("User Not Found");
                }
                List<UUID> usersId = workspaceRepository.getUserIdByWorkspaceId(workspace.getWorkspaceId());
                for (UUID userId : usersId) {
                    if (userId.equals(userAuthentication.getUserId())) {
                        throw new NotDuplicateException("You already invite this user");
                    }
                }
                workspaceRepository.addUserIdAndWorkspaceIdToUserWorkspaceForMember(userAuthentication.getUserId(), workspace.getWorkspaceId());
                emailService.inviteMemberByEmail(workspace, userAuthentication);
                List<Document> documents = documentRepository.getDocumentByWorkspaceId(workspace.getWorkspaceId());
                for (Document document : documents) {
                    documentRepository.addUserIdDocumentIdToUserDocument(userAuthentication.getUserId(), document.getDocumentId(), "VIEWER");
                }
            } else {
                throw new NotOwnerException("You are not the owner of this workspace");
            }
            return workspace;
        }
    }

    @Override
    public Boolean checkIsOwnerWorkspace(UUID workspaceId, UUID userId) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (userId == null) {
            throw new BadRequestException("User id cannot be blank or empty");
        } else if (userId.toString().isBlank()) {
            throw new BadRequestException("User id cannot be blank or empty");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }

        Workspace workspace = workspaceRepository.getWorkspaceById(workspaceId);
        User user = userRepository.getUserByUserId(userId);
        if (workspace == null) {
            throw new NotFoundException("Workspace doesn't exist");
        }
        if (user == null) {
            throw new NotFoundException("User doesn't exist");
        }
        System.out.println(workspaceRepository.checkIsOwner(workspaceId, userId));
        return workspaceRepository.checkIsOwner(workspaceId, userId);
    }

    @Override
    public Boolean checkAccessibility(UUID workspaceId) {
        if (workspaceId == null) {
            throw new BadRequestException("Workspace id cannot be null");
        } else if (workspaceId.toString().isBlank()) {
            throw new BadRequestException("Workspace id cannot be blank or empty");
        }
        Workspace workspace = workspaceRepository.getWorkspaceById(workspaceId);
        if (workspace == null) {
            throw new NotFoundException("Workspace doesn't exist");
        }
        return workspaceRepository.checkAccessibility(userAuthenticationService.getUserIdOfCurrentUser(), workspace.getWorkspaceId());
    }

    @Override
    public Integer getTotalPage(Integer pageSize) {
        Integer count = workspaceRepository.countWorkspace(userAuthenticationService.getUserIdOfCurrentUser());
        return (Integer) (int) Math.ceil((double) count / pageSize);
    }

    @Override
    public Boolean checkIsOwnerWorkspaceCurrenUser(UUID workspaceId) {
        return workspaceRepository.checkIsOwner(userAuthenticationService.getUserIdOfCurrentUser(), workspaceId);
    }


}
