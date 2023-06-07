package kh.com.kshrd.docengine.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Serial
    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        LocalDateTime currentTime = LocalDateTime.now();
        String errorMessage = "{ \"timestamp\": \"" + currentTime + "\", " +
                "\"status\": " + HttpServletResponse.SC_UNAUTHORIZED + ", " +
                "\"error\": \"Unauthorized\"}";

        PrintWriter writer = response.getWriter();
        writer.println(errorMessage);
    }
}
