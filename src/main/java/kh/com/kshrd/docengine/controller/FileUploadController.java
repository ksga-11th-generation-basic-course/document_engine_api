package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.com.kshrd.docengine.model.entity.FileImage;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.services.FileUploadFileService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/")
public class FileUploadController {
    private final FileUploadFileService fileUploadFileService;

    @PostMapping(path = "images/upload-image", consumes = {"multipart/form-data"})
    @Operation(summary = "Upload Image")
    public ResponseEntity<Response<FileImage>> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {
//        System.out.println(file);
        Response<FileImage> response = Response.<FileImage>builder()
                .message("Successfully")
                .payload(new FileImage("http://localhost:8080/api/v1/images/get-image?file=" + fileUploadFileService.saveFile(file)))
                .dateTime(LocalDateTime.now())
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("images/get-image")
    @Operation(summary = "Get Image")
    public ResponseEntity<Resource> getImage(
            @RequestParam("file") String fileName
    ) {
        Path path = Paths.get("src/main/resources/images/" + fileName);
        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new InputStreamResource(resource.getInputStream()));
        } catch (Exception e) {
            System.out.println("Error message {} " + e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }
}
