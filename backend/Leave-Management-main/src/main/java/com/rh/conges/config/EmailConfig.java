package com.rh.conges.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration for email sending
 *
 * NOTE: This configuration is currently disabled because it requires spring-boot-starter-mail dependency.
 *
 * To enable email functionality:
 * 1. Add the following dependency to pom.xml:
 *    <dependency>
 *        <groupId>org.springframework.boot</groupId>
 *        <artifactId>spring-boot-starter-mail</artifactId>
 *    </dependency>
 *
 * 2. Uncomment the code below
 * 3. Configure the email properties in application.properties:
 *    spring.mail.host=smtp.gmail.com
 *    spring.mail.port=587
 *    spring.mail.username=your-email@gmail.com
 *    spring.mail.password=your-app-password
 *    spring.mail.properties.mail.smtp.auth=true
 *    spring.mail.properties.mail.smtp.starttls.enable=true
 */
@Configuration
public class EmailConfig {

    // Uncomment the code below after adding spring-boot-starter-mail dependency

    /*
    @Value("${spring.mail.host:smtp.gmail.com}")
    private String host;

    @Value("${spring.mail.port:587}")
    private int port;

    @Value("${spring.mail.username:}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth:true}")
    private String auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}")
    private String starttls;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        if (username != null && !username.isEmpty()) {
            mailSender.setUsername(username);
            mailSender.setPassword(password);
        }

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", "false");

        return mailSender;
    }
    */
}

