package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.com.kshrd.docengine.model.Block;
import kh.com.kshrd.docengine.model.request.BlockRequest;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.services.BlockService;
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
public class BlockController {

    private final BlockService blockService;

    @PostMapping("blocks")
    @Operation(summary = "Creat Block")
    public ResponseEntity<Response<Block>> createBlock(@RequestBody BlockRequest blockRequest){
        Response<Block> response = Response.<Block>builder()
                .message("Create Block Successful")
                .payload(blockService.createBlock(blockRequest))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("blocks/{blockId}")
    @Operation(summary = "Edit Block")
    public ResponseEntity<Response<Block>> editBlock(@PathVariable UUID blockId, @RequestBody String content){
        Response<Block> response = Response.<Block>builder()
                .message("Edit Block Successful")
                .payload(blockService.editBlock(blockId, content))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("blocks/{blockId}")
    @Operation(summary = "Delete Block")
    public ResponseEntity<Response<Block>> deleteBlock(@PathVariable UUID blockId){
        blockService.deleteBlock(blockId);
        Response<Block> response = Response.<Block>builder()
                .message("Delete Block Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("blocks/get/block/For/each/document/{documentId}")
    @Operation(summary = "Get Block For Each Document")
    public ResponseEntity<Response<Block>> getBlockForEachDocument(@PathVariable UUID documentId){
        Response<Block> response = Response.<Block>builder()
                .message("Get Block For Each Document Successful")
                .payload(blockService.getBlockForEachDocument(documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }
}
