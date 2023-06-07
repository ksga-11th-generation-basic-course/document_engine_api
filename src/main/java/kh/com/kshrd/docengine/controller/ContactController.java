package kh.com.kshrd.docengine.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import kh.com.kshrd.docengine.model.request.ContactRequest;
import kh.com.kshrd.docengine.model.response.Response;
import kh.com.kshrd.docengine.services.ContactService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
//@CrossOrigin
@RequestMapping("/api/v1/")
public class ContactController {

    private final ContactService contactService;

    @PostMapping("contacts")
    @Operation(summary = "Contact Us")
    public ResponseEntity<?> contactUs(@RequestBody ContactRequest contactRequest) throws MessagingException {
        contactService.contactUs(contactRequest);
        Response<ContactRequest> response = Response.<ContactRequest>builder()
                .message("Contact Us Successful")
                .payload(null)
                .dateTime(LocalDateTime.now())
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok().body(response);
    }

}
