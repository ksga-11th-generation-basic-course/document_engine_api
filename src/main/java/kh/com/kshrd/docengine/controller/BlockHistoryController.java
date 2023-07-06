package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.com.kshrd.docengine.model.entity.Block;
import kh.com.kshrd.docengine.model.entity.BlockHistory;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.services.BlockHistoryService;
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
public class BlockHistoryController {

    final private BlockHistoryService blockHistoryService;

    @GetMapping("blocks/history/{historyId}")
    @Operation(summary = "Get Block History For Each History")
    public ResponseEntity<?> getBlockForEachDocument(@PathVariable UUID historyId){
        Response<List<BlockHistory>> response = Response.<List<BlockHistory>>builder()
                .message("Get Block For Each Document Successful")
                .payload(blockHistoryService.getBlockForEachHistory(historyId))
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }
}