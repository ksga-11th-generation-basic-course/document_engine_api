package kh.com.kshrd.docengine.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail notFoundHandler(NotFoundException notFoundException) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(

                HttpStatus.NOT_FOUND,

                notFoundException.getMessage()

        );

        problemDetail.setTitle("Not Found Data !!!");
        problemDetail.setType(URI.create("localhost:8000/error/not/found"));

        return problemDetail;
    }

    @ExceptionHandler(BadRequestException.class)
    ProblemDetail notFoundHandler(BadRequestException badRequestException) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(

                HttpStatus.BAD_REQUEST,

                badRequestException.getMessage()

        );

        problemDetail.setTitle("Bad Request !!!");
        problemDetail.setType(URI.create("localhost:8000/error/bad/request"));

        return problemDetail;
    }
}
