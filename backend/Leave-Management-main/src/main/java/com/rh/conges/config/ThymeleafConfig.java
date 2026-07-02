package com.rh.conges.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Thymeleaf template engine
 *
 * NOTE: This configuration is currently disabled because it requires spring-boot-starter-thymeleaf dependency.
 *
 * To enable Thymeleaf for email templates:
 * 1. Add the following dependency to pom.xml:
 *    <dependency>
 *        <groupId>org.springframework.boot</groupId>
 *        <artifactId>spring-boot-starter-thymeleaf</artifactId>
 *    </dependency>
 *
 * 2. Uncomment the code below
 * 3. Create email templates in src/main/resources/templates/email/
 */
@Configuration
public class ThymeleafConfig {

    // Uncomment the code below after adding spring-boot-starter-thymeleaf dependency

    /*
    @Bean
    public SpringResourceTemplateResolver emailTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(emailTemplateResolver());
        return templateEngine;
    }
    */
}

