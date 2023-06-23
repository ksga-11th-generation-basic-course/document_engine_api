package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.com.kshrd.docengine.model.entity.History;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.services.HistoryService;
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
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("histories/{documentId}")
    @Operation(summary = "Get History In Each Document")
    public ResponseEntity<?> getHistoryInEachDocument(@PathVariable UUID documentId){
        Response<List<History>> response = Response.<List<History>>builder()
                .message("Get History In Each Document Successful")
                .payload(historyService.getHistoryInEachDocument(documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }
    @PutMapping("histories/{historyId}/documents/{documentId}")
    @Operation(summary = "Restore Document")
    public ResponseEntity<?> restoreDocument(@PathVariable UUID historyId, @PathVariable UUID documentId){
        Response<String> response = Response.<String>builder()
                .message("Restore Document Successful")
                .payload(historyService.restoreDocument(historyId, documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("histories/{historyId}/")
    @Operation(summary = "Get History")
    public ResponseEntity<?> getHistoryByHistoryId(@PathVariable UUID historyId){
        Response<History> response = Response.<History>builder()
                .message("Restore Document Successful")
                .payload(historyService.getHistoryByHistoryId(historyId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("histories/{historyId}/documents/{documentId}")
    @Operation(summary = "Remove History")
    public ResponseEntity<?> removeHistory(@PathVariable UUID historyId ,@PathVariable UUID documentId){
        historyService.removeHistory(historyId, documentId);
        Response<History> response = Response.<History>builder()
                .message("Remove History Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }
}
