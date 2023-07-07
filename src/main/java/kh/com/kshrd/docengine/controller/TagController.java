package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.com.kshrd.docengine.model.entity.Tag;
import kh.com.kshrd.docengine.model.request.TagRequest;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.services.TagService;
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
public class TagController {

    private final TagService tagService;

    @PostMapping("tags")
    @Operation(summary = "Create Tag")
    public ResponseEntity<?> createTag(@RequestBody TagRequest tagRequest) {
        Response<Tag> response = Response.<Tag>builder()
                .message("Create Tag Successful")
                .payload(tagService.createTag(tagRequest))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("tags/{tagId}")
    @Operation(summary = "Edit Tag")
    public ResponseEntity<?> editTag(@PathVariable UUID tagId, @RequestParam String tagName) {
        Response<Tag> response = Response.<Tag>builder()
                .message("Edit Tag Successful")
                .payload(tagService.editTag(tagId, tagName))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("tags/{tagId}/document/{documentId}")
    @Operation(summary = "Delete Tag")
    public ResponseEntity<?> deleteTag(@PathVariable UUID tagId, @PathVariable UUID documentId){
        tagService.deleteTag(tagId, documentId);
        Response<Tag> response = Response.<Tag>builder()
                .message("Delete Tag Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("tags")
    @Operation(summary = "Get All Tag *")
    public ResponseEntity<?> getAllTag(){
        Response<List<Tag>> response = Response.<List<Tag>>builder()
                .message("Get All Tag Successful")
                .payload(tagService.getAllTag())
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("tags/workspace/{workspaceId}")
    @Operation(summary = "Get Tag In Each Workspace")
    public ResponseEntity<?> getTagInEachWorkspace(@PathVariable UUID workspaceId){
        Response<List<Tag>> response = Response.<List<Tag>>builder()
                .message("Get Tag In Each Workspace Successful")
                .payload(tagService.getTagInEachWorkspace(workspaceId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("tags/add")
    @Operation(summary = "Add Tag For Document")
    public ResponseEntity<?> addTagsForDocument(@RequestParam UUID tagId, @RequestParam UUID documentId, @RequestParam UUID workspaceId){
        UUID tag = tagService.addTagsForDocument(tagId, documentId, workspaceId);
        Response<Tag> response = Response.<Tag>builder()
                .message("Add Tag Successful")
                .payload(tagService.getTagByTagId(tag, workspaceId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("tags/document/{documentId}")
    @Operation(summary = "Get Tag By Document Id")
    public ResponseEntity<?> getTagFromTagDocument(@PathVariable UUID documentId){
        Response<List<Tag>> response = Response.<List<Tag>>builder()
                .message("Get Tag By Document Id Successful")
                .payload(tagService.getTagFromTagDocument(documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("tags/history/{historyId}")
    @Operation(summary = "Get Tag By History Id")
    public ResponseEntity<?> getTagFromHistoryId(@PathVariable UUID historyId){
        Response<List<Tag>> response = Response.<List<Tag>>builder()
                .message("Get Tag By Document Id Successful")
                .payload(tagService.getTagFromHistoryId(historyId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }
}