package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.com.kshrd.docengine.model.Workspace;
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
@CrossOrigin
@RequestMapping("/api/v1/workspace")
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    @PostMapping("/create")
    @Operation(summary = "Create Workspace")
    public ResponseEntity<Response<Workspace>> createWorkspace(@RequestBody WorkspaceRequest workspaceRequest){
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Create Workspace Successfully")
                .payload(workspaceService.createWorkspace(workspaceRequest))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/join")
    @Operation(summary = "Join Workspace")
    public ResponseEntity<Response<Workspace>> joinWorkspace(@RequestParam String workspaceCode){
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Join Workspace Successfully")
                .payload(workspaceService.joinWorkspace(workspaceCode))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/leave")
    @Operation(summary = "Leave Workspace")
    public ResponseEntity<Response<Workspace>> leaveWorkspace(@RequestParam UUID workspaceId){
        workspaceService.leaveWorkspace(workspaceId);
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Leaved Workspace Successfully")
                .payload(null)
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Remove Workspace")
    public ResponseEntity<Response<Workspace>> removeWorkspace(@RequestParam UUID workspaceId){
        workspaceService.removeWorkspace(workspaceId);
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Remove Workspace Successfully")
                .payload(null)
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/remove-member")
    @Operation(summary = "Remove Member From Workspace")
    public ResponseEntity<Response<Workspace>> removeMemberFromWorkspace(@RequestParam UUID userId,@RequestParam UUID workspaceId){
        workspaceService.removeMemberFromWorkspace(userId,workspaceId);
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Remove Member Successfully")
                .payload(null)
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/set-accessibility")
    @Operation(summary = "Set Accessibility")
    public ResponseEntity<Response<Workspace>> setAccessibilityToUser(@RequestParam UUID userId,@RequestParam UUID workspaceId,@RequestParam Boolean status){
        workspaceService.setAccessibilityToUser(userId,workspaceId,status);
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Set Accessibility Successfully")
                .payload(null)
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/name")
    @Operation(summary = "Update Workspace Name")
    public ResponseEntity<Response<Workspace>> updateWorkspaceName(@RequestParam String name,@RequestParam UUID workspaceId){
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Update Workspace Name Successfully")
                .payload(workspaceService.updateWorkspaceName(name,workspaceId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/image")
    @Operation(summary = "Update Workspace Image")
    public ResponseEntity<Response<Workspace>> updateWorkspaceImage(@RequestParam String Image,@RequestParam UUID workspaceId){
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Update Workspace Image Successfully")
                .payload(workspaceService.updateWorkspaceImage(Image,workspaceId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/all")
    @Operation(summary = "Get All Workspace")
    public ResponseEntity<Response<List<Workspace>>> getAllWorkspace(){
        Response<List<Workspace>> response = Response.<List<Workspace>>builder()
                .message("Get All Workspace Successfully")
                .payload(workspaceService.getAllWorkspace())
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/count-document/{workspaceId}")
    @Operation(summary = "Get Total Document")
    public ResponseEntity<Response<Integer>> getTotalOfDocument(@PathVariable UUID workspaceId){
        Response<Integer> response = Response.<Integer>builder()
                .message("Get Total Document Successfully")
                .payload(workspaceService.getTotalOfDocument(workspaceId))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter Workspace")
    public ResponseEntity<Response<List<Workspace>>> filterWorkspace(@RequestParam Boolean filter){
        Response<List<Workspace>> response = Response.<List<Workspace>>builder()
                .message("Get Total Document Successfully")
                .payload(workspaceService.filterWorkspace(filter))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/search/{input}")
    @Operation(summary = "Search Workspace")
    public ResponseEntity<Response<List<Workspace>>> searchWorkspace(@PathVariable String input){
        Response<List<Workspace>> response = Response.<List<Workspace>>builder()
                .message("Get Total Document Successfully")
                .payload(workspaceService.searchWorkspace(input))
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/image")
    @Operation(summary = "Delete Workspace Image")
    public ResponseEntity<Response<Workspace>> deleteWorkspaceImage(@RequestParam UUID workspaceId){
        workspaceService.deleteWorkspaceImage(workspaceId);
        Response<Workspace> response = Response.<Workspace>builder()
                .message("Delete Workspace Image Successfully")
                .payload(null)
                .status(HttpStatus.OK)
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

}
