package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.com.kshrd.docengine.enums.EAccessibility;
import kh.com.kshrd.docengine.enums.ESortCurrentDateTime;
import kh.com.kshrd.docengine.model.entity.Document;
import kh.com.kshrd.docengine.model.request.DocumentRequest;
import kh.com.kshrd.docengine.model.response.DocumentResponse;
import kh.com.kshrd.docengine.model.response.MemberResponse;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.services.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
//@CrossOrigin
@RequestMapping("/api/v1/")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("documents")
    @Operation(summary = "Create Document")
    public ResponseEntity<?> createDocument(@RequestBody DocumentRequest documentRequest) {
        Document document = documentService.createDocument(documentRequest);
        Response<Document> response = Response.<Document>builder()
                .message("Create Document Successful")
                .payload(documentService.getDocumentByDocumentId(document.getDocumentId()))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.CREATED)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("documents/{documentId}")
    @Operation(summary = "Edit Document")
    public ResponseEntity<?> editDocument(@PathVariable UUID documentId, @RequestParam String title) {
        Document document = documentService.editDocument(documentId, title);
        LocalDateTime editDate = documentService.getEditDate(documentId);
        Response<DocumentResponse> response = Response.<DocumentResponse>builder()
                .message("Edit Document Successful")
                .payload(new DocumentResponse(document.getDocumentId(), document.getTitle(), document.getStatus(), document.getCreatedDate(), document.getPages(), document.getWorkspaceId(), document.getTags(), document.getBlocks(), documentService.recently(editDate)))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }


    @PutMapping("documents/{documentId}/editing")

    @Operation(summary = "Current Editing Document")
    public ResponseEntity<?> editDocument(@PathVariable UUID documentId) {
        documentService.currentEditing(documentId);
        Response<Document> response = Response.<Document>builder()
                .message("Set Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("documents/{documentId}/users/{userId}/accessibility")
    @Operation(summary = "Set Accessibility")
    public ResponseEntity<?> setAccessibility(@PathVariable UUID documentId, @PathVariable UUID userId, @PathVariable UUID workspaceId, @RequestParam EAccessibility accessibility) {
        documentService.setAccessibility(documentId, userId, workspaceId, accessibility);
        Response<Document> response = Response.<Document>builder()
                .message("Set Accessibility Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("documents/{documentId}/view")
    @Operation(summary = "View Document")
    public ResponseEntity<?> viewDocument(@PathVariable UUID documentId) {
        Response<Document> response = Response.<Document>builder()
                .message("View Document Successful")
                .payload(documentService.viewDocument(documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("documents/workspaces/{workspaceId}")
    @Operation(summary = "Get Document In Each Workspace")
    public ResponseEntity<?> getDocumentInEachWorkspace(@PathVariable UUID workspaceId, @RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "5") Integer pageSize, @RequestParam ESortCurrentDateTime eSortCurrentDateTime) {
        Response<List<DocumentResponse>> response = Response.<List<DocumentResponse>>builder()
                .message("Get Document In Each Workspace Successful")
                .payload(documentService.getDocumentInEachWorkspace(workspaceId, pageNo, pageSize, eSortCurrentDateTime))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("documents/{documentId}/duplicate")
    @Operation(summary = "Duplicate Document")
    public ResponseEntity<?> duplicateDocument(@PathVariable UUID documentId) {
        Response<Document> response = Response.<Document>builder()
                .message("Duplicate Document Successful")
                .payload(documentService.duplicateDocument(documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("documents/workspaces/{workspaceId}/filter/tag")
    @Operation(summary = "Search Document By TagName *")
    public ResponseEntity<?> searchDocumentByTagName(@PathVariable UUID workspaceId, @RequestParam String tagName) {
        Response<List<Document>> response = Response.<List<Document>>builder()
                .message("Search Document Successful")
                .payload(documentService.searchDocumentByTagName(workspaceId, tagName))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("documents/{documentId}")
    @Operation(summary = "Delete Document")
    public ResponseEntity<?> deleteDocument(@PathVariable UUID documentId) {
        documentService.deleteDocument(documentId);
        Response<Document> response = Response.<Document>builder()
                .message("Delete Document Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("documents/{workspaceId}/filter/tags")
    @Operation(summary = "Search Document By Many TagName")
    public ResponseEntity<?> searchDocumentByTagName(@PathVariable UUID workspaceId, @RequestParam List<String> tags) {
        Response<Set<Document>> response = Response.<Set<Document>>builder()
                .message("Search Document Successful")
                .payload(documentService.searchDocumentByManyTagName(workspaceId, tags))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("documents/{documentId}/member")
    @Operation(summary = "Get All Member In Each Document")
    public ResponseEntity<?> getAllMemberInEachDocument(@PathVariable UUID documentId) {
        Response<List<MemberResponse>> response = Response.<List<MemberResponse>>builder()
                .message("Get All Member In Each Document Successful")
                .payload(documentService.getAllMemberInEachDocument(documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("documents/{documentId}")
    @Operation(summary = "Get Document By Document Id")
    public ResponseEntity<?> getDocumentByDocumentId(@PathVariable UUID documentId) {
        Response<Document> response = Response.<Document>builder()
                .message("Get Document By Document By Id")
                .payload(documentService.getDocumentByDocumentId(documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("documents/{documentId}/username")
    @Operation(summary = "Get Username By Document Id")
    public ResponseEntity<?> getUsernameByDocumentId(@PathVariable UUID documentId) {
        Response<String> response = Response.<String>builder()
                .message("Get Username By Document By Id")
                .payload(documentService.getUserByDocumentId(documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("documents/{documentId}/workspace")
    @Operation(summary = "Get Workspace Name By Document Id")
    public ResponseEntity<?> getWorkspaceNameByDocumentId(@PathVariable UUID documentId) {
        Response<String> response = Response.<String>builder()
                .message("Get Username By Document By Id")
                .payload(documentService.getWorkspaceNameByDocumentId(documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }
}

