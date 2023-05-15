package kh.com.kshrd.docengine.exceptions;


import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@ControllerAdvice
public class GlobalException extends ResponseEntityExceptionHandler {


    /* Not Verify Exception*/
    @ExceptionHandler(NotVerifyException.class)
    public ProblemDetail notVerifyHandler(NotVerifyException notVerifyException) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(

                HttpStatus.NOT_FOUND,

                notVerifyException.getMessage()

        );

        problemDetail.setTitle("Not Verify !!!");
        problemDetail.setType(URI.create("localhost:8000/error/not/verify"));

        return problemDetail;
    }

    /* Not Found Exception*/
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail notFoundHandler(NotFoundException notFoundException) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(

                HttpStatus.NOT_FOUND,

                notFoundException.getMessage()

        );

        problemDetail.setTitle("Not Found Data !!!");
        problemDetail.setType(URI.create("localhost:8000/error/not/found"));

        return problemDetail;
    }


    /* Bad Request Exception*/
    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail badRequestHandler(BadRequestException badRequestException) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(

                HttpStatus.BAD_REQUEST,

                badRequestException.getMessage()

        );

        problemDetail.setTitle("Bad Request !!!");
        problemDetail.setType(URI.create("localhost:8000/error/bad/request"));

        return problemDetail;
    }

    /* Value not equal Exception*/
    @ExceptionHandler(ValueNotEqualException.class)
    public ProblemDetail valueNotEqualHandler(ValueNotEqualException valueNotEqualException) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(

                HttpStatus.BAD_REQUEST,

                valueNotEqualException.getMessage()

        );

        problemDetail.setTitle("Value not equal !!!");
        problemDetail.setType(URI.create("localhost:8000/error/value/not/equal"));

        return problemDetail;
    }

    /* handle MethodArgument NotValid Exception*/
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Timestamp", LocalDateTime.now());
        body.put("Status", status.value());
        body.put("Error", Objects.requireNonNull(ex.getFieldError()).getDefaultMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(NotEditorException.class)
    public ProblemDetail notEditor(NotEditorException notEditorException) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, notEditorException.getMessage());
        problemDetail.setType(URI.create("localhost:8000/error/not/editor"));
        problemDetail.setTitle("Your accessibility is not editor");
        return problemDetail;
    }

    @ExceptionHandler(NotOwnerException.class)
    public ProblemDetail problem(NotOwnerException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, exception.getMessage()
        );
        problemDetail.setType(URI.create("localhost:8080/error/forbidden"));
        problemDetail.setTitle("You are not owner");
        return problemDetail;
    }

    @ExceptionHandler(NotDuplicateException.class)
    public ProblemDetail notDuplicate(NotDuplicateException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, exception.getMessage()
        );
        problemDetail.setType(URI.create("localhost:8080/error/conflict"));
        problemDetail.setTitle("This tag has already");
        return problemDetail;
    }
}
