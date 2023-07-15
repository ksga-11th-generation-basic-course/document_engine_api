package kh.com.kshrd.docengine.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadFileService {
    String saveFile(MultipartFile file);
}
