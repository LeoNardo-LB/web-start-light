package smm.archetype;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@EnableAspectJAutoProxy
@RestController
@SpringBootApplication
@EnableJpaAuditing
public class WebStartLightApplication implements CommandLineRunner {

    @Value("${server.port}")
    public String port;

    @Value("${server.servlet.context-path}")
    public String contextPath;

    @Value("${spring.application.name}")
    public String appName;

    @Value("${springdoc.swagger-ui.path}")
    public String openapiUrl;

    @Value("${springdoc.api-docs.path}")
    public String apiDocUrl;
    public static void main(String[] args) {
        SpringApplication.run(WebStartLightApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("[{}]应用启动成功!", appName);
        log.info("Request URL: {}", String.format("http://127.0.0.1:%s%s", port, contextPath));
        log.info("Swagger URL: {}", String.format("http://127.0.0.1:%s%s%s", port, contextPath, openapiUrl));
        log.info("API-Doc URL: {}", String.format("http://127.0.0.1:%s%s%s", port, contextPath, apiDocUrl));
    }

}
