package kh.com.kshrd.docengine;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer")
@OpenAPIDefinition(info = @Info(title = "DocEngine", version = "1.0", description = "A to-do list simple and effective tool for keeping track of the thing you need to do"))
public class DocEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocEngineApplication.class, args);
    }
}
