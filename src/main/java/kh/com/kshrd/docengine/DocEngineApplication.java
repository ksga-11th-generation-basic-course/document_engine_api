package kh.com.kshrd.docengine;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer")
@OpenAPIDefinition(info = @Info(title = "DocEngine", version = "1.0", description = "DocEngine is a platform that gives users the ability to create documents and manage those documents within the team \n" +
        "."))
public class DocEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocEngineApplication.class, args);
    }
}
