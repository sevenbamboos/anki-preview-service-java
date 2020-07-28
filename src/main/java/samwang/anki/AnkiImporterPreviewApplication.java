package samwang.anki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class AnkiImporterPreviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnkiImporterPreviewApplication.class, args);
    }

    private static List<String> trustedOrigins = new ArrayList<>();
    static {
        trustedOrigins.add("http://localhost:8081");
        trustedOrigins.add("http://localhost:5000");
        trustedOrigins.add("http://localhost:3000");
    };

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        trustedOrigins.forEach(x -> System.out.println("Trusted origin:" + x));
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                CorsRegistration cors = registry.addMapping("/**");
                trustedOrigins.forEach(x -> cors.allowedOrigins(x));
            }
        };
    }
}
