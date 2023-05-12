package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.com.kshrd.docengine.model.Tag;
import kh.com.kshrd.docengine.model.request.TagRequest;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/")
public class TagController {

    private final TagService tagService;

    @PostMapping("tags")
    @Operation(summary = "Creating Tag")
    public ResponseEntity<Response<Tag>> createTag(@RequestBody TagRequest tagRequest) {
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
    public ResponseEntity<Response<Tag>> editTag(@PathVariable UUID tagId, @RequestBody TagRequest tagRequest) {
        Response<Tag> response = Response.<Tag>builder()
                .message("Edit Tag Successful")
                .payload(tagService.editTag(tagId, tagRequest))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("tags/{tagId}")
    @Operation(summary = "Delete Tag")
    public ResponseEntity<Response<Tag>> deleteTag(@PathVariable UUID tagId){
        tagService.deleteTag(tagId);
        Response<Tag> response = Response.<Tag>builder()
                .message("Delete Tag Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }
}
