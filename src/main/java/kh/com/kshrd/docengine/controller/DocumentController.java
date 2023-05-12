package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.com.kshrd.docengine.model.Document;
import kh.com.kshrd.docengine.model.request.DocumentRequest;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.services.DocumentService;
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
@RequestMapping("/api/v1/")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("documents")
    @Operation(summary = "Create Document")
    public ResponseEntity<Response<Document>> createDocument(@RequestBody DocumentRequest documentRequest){
        Document document = documentService.createDocument(documentRequest);
        Response<Document> response = Response.<Document>builder()
                .message("Create Document Successful")
                .payload(documentService.getDocumentById(document.getDocumentId()))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("documents/{documentId}")
    @Operation(summary = "Edit Document")
    public ResponseEntity<Response<Document>> editDocument(@PathVariable UUID documentId, @RequestParam String title){
        Response<Document> response = Response.<Document>builder()
                .message("Edit Document Successful")
                .payload(documentService.editDocument(documentId, title))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("documents/{documentId}/current-editing")
    @Operation(summary = "Current Editing Document")
    public ResponseEntity<Response<Document>> editDocument(@PathVariable UUID documentId){
        documentService.currentEditing(documentId);
        Response<Document> response = Response.<Document>builder()
                .message("Set Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("documents/{documentId}/set-accessibility")
    @Operation(summary = "Set Accessibility")
    public ResponseEntity<Response<Document>> setAccessibility(@RequestParam UUID documentId, @RequestParam UUID userId, @RequestParam String accessibility){
        documentService.setAccessibility(documentId, userId, accessibility);
        Response<Document> response = Response.<Document>builder()
                .message("Set Accessibility Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("documents")
    @Operation(summary = "Getting All Document")
    public ResponseEntity<Response<List<Document>>> getAllDocument(){
        Response<List<Document>> response = Response.<List<Document>>builder()
                .message("Get All Document Successful")
                .payload(documentService.getAllDocument())
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("documents/{documentId}")
    @Operation(summary = "View Document")
    public ResponseEntity<Response<Document>> viewDocument(@PathVariable UUID documentId){
        Response<Document> response = Response.<Document>builder()
                .message("View Document Successful")
                .payload(documentService.viewDocument(documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("documents/{workspaceId}/workspace-document")
    @Operation(summary = "Get Document In Each Workspcae")
    public ResponseEntity<Response<List<Document>>> getDocumentInEachWorkspace(@PathVariable UUID workspaceId){
        Response<List<Document>> response = Response.<List<Document>>builder()
                .message("Get Document In Each Workspace Successful")
                .payload(documentService.getDocumentInEachWorkspace(workspaceId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("documents/{documentId}/duplicate-document")
    @Operation(summary = "Duplicate Document")
    public ResponseEntity<Response<Document>> duplicateDocument(@PathVariable UUID documentId){
        Response<Document> response = Response.<Document>builder()
                .message("Duplicate Document Successful")
                .payload(documentService.duplicateDocument(documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("documents/{}")
}
