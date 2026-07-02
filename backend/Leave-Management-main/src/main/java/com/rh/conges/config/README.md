# Configuration Classes

This directory contains all configuration classes for the Leave Management System.

## Configuration Files

### 1. WebMvcConfig.java
- Configures CORS (Cross-Origin Resource Sharing) for the REST API
- Allows requests from frontend applications (localhost:3000, localhost:4200)
- Enables methods: GET, POST, PUT, DELETE, OPTIONS

### 2. SecurityConfig.java
- Configures Spring Security
- Disables CSRF for REST API (stateless authentication)
- Defines public endpoints (auth, H2 console, Swagger)
- Configures BCrypt password encoder
- **Note:** For production, implement proper JWT authentication

### 3. AsyncConfig.java
- Enables asynchronous task execution
- Configures thread pool for async operations
- Used by NotificationService for sending emails asynchronously

### 4. EmailConfig.java
- Configures JavaMailSender for email functionality
- Supports Gmail SMTP by default
- **Requirements:** 
  - Add `spring-boot-starter-mail` dependency to pom.xml
  - Configure mail properties in application.properties

### 5. ThymeleafConfig.java
- Configures Thymeleaf template engine for email templates
- Sets up template resolver for HTML email templates
- **Requirements:**
  - Add `spring-boot-starter-thymeleaf` dependency to pom.xml

### 6. SwaggerConfig.java
- Configures OpenAPI/Swagger documentation
- Provides interactive API documentation at /swagger-ui/index.html
- Includes JWT bearer token authentication support
- **Requirements:**
  - Add `springdoc-openapi-starter-webmvc-ui` dependency to pom.xml

### 7. AuditConfig.java
- Enables JPA auditing for entity tracking
- Automatically tracks who created/modified entities and when
- Uses Spring Security context to get current user

### 8. SchedulingConfig.java
- Enables scheduled tasks (@Scheduled annotation)
- Used for periodic tasks like:
  - Sending reminders for pending leave requests
  - Checking for upcoming leaves
  - Generating reports

### 9. ApplicationConfig.java
- Application-specific configuration properties
- Default values that can be overridden in application.properties
- Properties:
  - `application.name`: Application name
  - `application.url`: Base URL for the application
  - `application.leaveRequestReminderDays`: Days before sending reminders
  - `application.lowBalanceThreshold`: Threshold for low balance alerts

## Required Dependencies

Some configuration files require additional dependencies in `pom.xml`:

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Email Support (Optional) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- Thymeleaf for Email Templates (Optional) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- OpenAPI/Swagger Documentation (Optional) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

## Application Properties

Add these properties to `src/main/resources/application.properties`:

```properties
# Application Configuration
application.name=Leave Management System
application.url=http://localhost:8080
application.leaveRequestReminderDays=3
application.lowBalanceThreshold=5

# Email Configuration (if using EmailConfig)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# H2 Console (for development)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
```

## Notes

- For production deployment, review and strengthen security configurations
- Implement proper JWT authentication instead of disabling security
- Configure proper CORS origins instead of using wildcards
- Use environment variables for sensitive data (passwords, API keys)
- Enable HTTPS/TLS for production environments

