package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.com.kshrd.docengine.model.entity.Block;
import kh.com.kshrd.docengine.model.request.BlockRequest;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.services.BlockService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
//@CrossOrigin
@RequestMapping("/api/v1/")
public class BlockController {

    private final BlockService blockService;

    @PostMapping("blocks")
    @Operation(summary = "Create Block")
    public ResponseEntity<?> createBlock(@RequestBody BlockRequest blockRequest){
        Response<Block> response = Response.<Block>builder()
                .message("Create Block Successful")
                .payload(blockService.createBlock(blockRequest))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.CREATED)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("blocks/{blockId}/documents/{documentId}")
    @Operation(summary = "Edit Block")
    public ResponseEntity<Response<Block>> editBlock(@PathVariable UUID blockId, @PathVariable UUID documentId ,@RequestBody Map<String, Object> content){
        Response<Block> response = Response.<Block>builder()
                .message("Edit Block Successful")
                .payload(blockService.editBlock(blockId, documentId, content))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("blocks/{blockId}/documents/{documentId}")
    @Operation(summary = "Delete Block")
    public ResponseEntity<Response<Block>> deleteBlock(@PathVariable UUID blockId, @PathVariable UUID documentId){
        blockService.deleteBlock(blockId, documentId);
        Response<Block> response = Response.<Block>builder()
                .message("Delete Block Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("blocks/document/{documentId}")
    @Operation(summary = "Get Block For Each Document")
    public ResponseEntity<Response<List<Block>>> getBlockForEachDocument(@PathVariable UUID documentId){
        Response<List<Block>> response = Response.<List<Block>>builder()
                .message("Get Block For Each Document Successful")
                .payload(blockService.getBlockForEachDocument(documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

}
