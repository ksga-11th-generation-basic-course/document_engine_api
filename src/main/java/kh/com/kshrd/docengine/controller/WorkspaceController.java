package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import kh.com.kshrd.docengine.enums.ESortCurrentDateTime;
import kh.com.kshrd.docengine.model.response.MemberResponse;
import kh.com.kshrd.docengine.model.entity.Workspace;
import kh.com.kshrd.docengine.model.request.WorkspaceRequest;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.services.WorkspaceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
//@CrossOrigin
@RequestMapping("/api/v1/")
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    @PostMapping("workspaces")
    @Operation(summary = "Create Workspace")
    public ResponseEntity<?> createWorkspace(@RequestBody WorkspaceRequest workspaceRequest) {
        Workspace workspace = workspaceService.createWorkspace(workspaceRequest);
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Create Workspace Successfully")
                .payload(workspaceService.getWorkspaceByWorkspaceId(workspace.getWorkspaceId()))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("workspaces/member")
    @Operation(summary = "Join Workspace")
    public ResponseEntity<?> joinWorkspace(@RequestParam String workspaceCode) {
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Join Workspace Successfully")
                .payload(workspaceService.joinWorkspace(workspaceCode))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("workspaces/leave")
    @Operation(summary = "Leave Workspace")
    public ResponseEntity<?> leaveWorkspace(@RequestParam UUID workspaceId) {
        workspaceService.leaveWorkspace(workspaceId);
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Leaved Workspace Successfully")
                .payload(null)
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("workspaces/{workspaceId}")
    @Operation(summary = "Remove Workspace")
    public ResponseEntity<?> removeWorkspace(@PathVariable UUID workspaceId) {
        workspaceService.removeWorkspace(workspaceId);
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Remove Workspace Successfully")
                .payload(null)
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("workspaces/member")
    @Operation(summary = "Remove Member From Workspace")
    public ResponseEntity<?> removeMemberFromWorkspace(@RequestParam UUID userId, @RequestParam UUID workspaceId) {
        workspaceService.removeMemberFromWorkspace(userId, workspaceId);
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Remove Member Successfully")
                .payload(null)
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("workspaces/accessibility")
    @Operation(summary = "Set Accessibility")
    public ResponseEntity<?> setAccessibilityToUser(@RequestParam UUID userId, @RequestParam UUID workspaceId, @RequestParam Boolean status) {
        Response<MemberResponse> response = Response.<MemberResponse>builder()
                .message("Set Accessibility Successfully")
                .payload(workspaceService.setAccessibilityToUser(userId, workspaceId, status))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("workspaces")
    @Operation(summary = "Get All Workspace")
    public ResponseEntity<?> getAllWorkspace(@RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "5") Integer pageSize, @RequestParam(defaultValue = "false") Boolean asc, @RequestParam(defaultValue = "false") Boolean desc, @RequestParam ESortCurrentDateTime eSortWorkspace) {
        Response<List<Workspace>> response = Response.<List<Workspace>>builder()
                .message("Get All Workspace Successfully")
                .payload(workspaceService.getAllWorkspace(pageNo, pageSize, asc, desc, eSortWorkspace))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("workspaces/{workspaceId}/total")
    @Operation(summary = "Get Total Document *")
    public ResponseEntity<?> getTotalOfDocument(@PathVariable UUID workspaceId) {
        Response<Integer> response = Response.<Integer>builder()
                .message("Get Total Document Successfully")
                .payload(workspaceService.getTotalOfDocument(workspaceId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("workspaces/filter")
    @Operation(summary = "Filter Owner Workspace Or Another Workspace")
    public ResponseEntity<?> filterWorkspace(@RequestParam Boolean filter, @RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "5") Integer pageSize, @RequestParam(defaultValue = "false") Boolean asc, @RequestParam(defaultValue = "false") Boolean desc, @RequestParam ESortCurrentDateTime eSortWorkspace) {
        Response<List<Workspace>> response = Response.<List<Workspace>>builder()
                .message("Get Total Document Successfully")
                .payload(workspaceService.filterWorkspace(filter, pageNo, pageSize, asc, desc, eSortWorkspace))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("workspaces/search")
    @Operation(summary = "Search Workspace")
    public ResponseEntity<?> searchWorkspace(@RequestParam String workspaceName) {
        Response<List<Workspace>> response = Response.<List<Workspace>>builder()
                .message("Get Total Document Successfully")
                .payload(workspaceService.searchWorkspace(workspaceName))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("workspaces/image")
    @Operation(summary = "Delete Workspace Image")
    public ResponseEntity<?> deleteWorkspaceImage(@RequestParam UUID workspaceId) {
        Workspace workspace = workspaceService.deleteWorkspaceImage(workspaceId);
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Delete Workspace Image Successfully")
                .payload(workspaceService.getWorkspaceByWorkspaceId(workspace.getWorkspaceId()))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("workspaces/{workspaceId}")
    @Operation(summary = "Edit Workspace")
    public ResponseEntity<?> editWorkspace(@PathVariable UUID workspaceId, @RequestBody WorkspaceRequest workspaceRequest) {
        workspaceService.editWorkspace(workspaceId, workspaceRequest);
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Update Workspace Successfully")
                .payload(workspaceService.getWorkspaceById(workspaceId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("workspaces/{workspaceId}/member")
    @Operation(summary = "Get All Member In Each Workspace")
    public ResponseEntity<?> getAllMemberInEachWorkspace(@PathVariable UUID workspaceId) {
        Response<List<MemberResponse>> response = Response.<List<MemberResponse>>builder()
                .message("Get All Member In Each Workspace Successfully")
                .payload(workspaceService.getAllMemberInEachWorkspace(workspaceId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("workspaces/{workspaceId}/isOwner")
    @Operation(summary = "Get Workspace By WorkspaceId")
    public ResponseEntity<?> getWorkspaceByWorkspaceId(@PathVariable UUID workspaceId) {
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Get All Member In Each Workspace Successfully")
                .payload(workspaceService.getWorkspaceByWorkspaceId(workspaceId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("workspaces/{workspaceId}")
    @Operation(summary = "Get Workspace By WorkspaceId")
    public ResponseEntity<?> getWorkspaceById(@PathVariable UUID workspaceId) {
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Get All Member In Each Workspace Successfully")
                .payload(workspaceService.getWorkspaceById(workspaceId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("workspaces/{workspaceId}/invite")
    @Operation(summary = "Invite To Join Workspace By Email")
    public ResponseEntity<?> inviteMemberByEmail(@PathVariable UUID workspaceId, @RequestParam String email) throws MessagingException {
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Invite Successfully")
                .payload(workspaceService.inviteMemberByEmail(workspaceId, email))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("workspaces/{workspaceId}/user/{userId}")
    @Operation(summary = "Check Is Owner")
    public ResponseEntity<?> checkIsOwnerWorkspace(@PathVariable UUID workspaceId, @PathVariable UUID userId){
        Response<Boolean> response = Response.<Boolean>builder()
                .message("Invite Successfully")
                .payload(workspaceService.checkIsOwnerWorkspace(workspaceId, userId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("workspaces/{workspaceId}/check/accessibility")
    @Operation(summary = "Check Accessibility")
    public ResponseEntity<?> checkAccessibility(@PathVariable UUID workspaceId){
        Response<Boolean> response = Response.<Boolean>builder()
                .message("Check Accessibility Successfully")
                .payload(workspaceService.checkAccessibility(workspaceId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("workspaces/totalPage")
    @Operation(summary = "Get Total page")
    public ResponseEntity<?> getTotalPage(@RequestParam Integer pageSize){
        Response<Integer> response = Response.<Integer>builder()
                .message("Check Accessibility Successfully")
                .payload(workspaceService.getTotalPage(pageSize))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("workspaces/{workspaceId}/current/user")
    @Operation(summary = "Check Is Owner")
    public ResponseEntity<?> checkIsOwnerWorkspaceCurrenUser(@PathVariable UUID workspaceId){
        Response<Boolean> response = Response.<Boolean>builder()
                .message("Invite Successfully")
                .payload(workspaceService.checkIsOwnerWorkspaceCurrenUser(workspaceId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }
}
