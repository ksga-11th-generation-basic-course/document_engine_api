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
@CrossOrigin
@RequestMapping("/api/v1/")
public class HistoryController {

    private final HistoryService historyService;

    /*
     endpoint for get history in each document
    {
     url : http://localhost:8080/api/v1/histories/{{documentId}}
    }
    */
    @GetMapping("histories/{documentId}")
    @Operation(summary = "Get History In Each Document")
    public ResponseEntity<Response<List<History>>> getHistoryInEachDocument(@PathVariable UUID documentId) {
        Response<List<History>> response = Response.<List<History>>builder()
                .message("Get History In Each Document Successful")
                .payload(historyService.getHistoryInEachDocument(documentId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    /*
     endpoint for restore document
    {
      url : http://localhost:8080/api/v1/histories/restore/{{historyId}}/documents/{{documentId}}
    }
    */
    @PutMapping("histories/restore/{historyId}/documents/{documentId}")
    @Operation(summary = "Restore Document")
    public ResponseEntity<Response<History>> restoreDocument(@PathVariable UUID historyId, @PathVariable UUID documentId) {
        historyService.restoreDocument(historyId, documentId);
        Response<History> response = Response.<History>builder()
                .message("Restore Document Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    /*
     endpoint for get history by history id
    {
      url : http://localhost:8080/api/v1/histories/{{historyId}}
    }
    */
    @GetMapping("histories/{historyId}")
    @Operation(summary = "Get History")
    public ResponseEntity<Response<History>> getHistoryByHistoryId(@PathVariable UUID historyId) {
        Response<History> response = Response.<History>builder()
                .message("Restore Document Successful")
                .payload(historyService.getHistoryByHistoryId(historyId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

    /*
     endpoint for remove history
    {
      url : http://localhost:8080/api/v1/histories/{{historyId}}
    }
    */
    @DeleteMapping("histories/{historyId}")
    @Operation(summary = "Remove History")
    public ResponseEntity<Response<History>> removeHistory(@PathVariable UUID historyId) {
        historyService.removeHistory(historyId);
        Response<History> response = Response.<History>builder()
                .message("Remove History Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }
}
