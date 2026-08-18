# Spring Boot Annotations Notes

## 1. Starting Class Annotations

Also called the **Main Class** or **Bootstrapping Class**.

### `@SpringBootApplication`

This is the main annotation in a Spring Boot application.

It tells Spring Boot:

> "This is the starting point of my Spring Boot application. Configure everything automatically and start the application."

It combines three annotations into one:

### 1. `@Configuration`

This tells Spring:

> "This class contains configuration for the Spring application. Spring, read this class while setting up the application."

### 2. `@EnableAutoConfiguration`

It tells Spring Boot:

> "Automatically configure the application based on the dependencies present in `pom.xml`."

### 3. `@ComponentScan`

This tells Spring:

> "Search for Spring components in this package and all its sub-packages."

When the application starts, Spring automatically scans:

* `@Controller`
* `@RestController`
* `@Service`
* `@Repository`
* `@Component`

and creates their objects (**beans**) in the **Spring IoC Container**.

---

# 2. Entity Class Annotations

## `@Entity`

`@Entity` is a JPA annotation used to mark a Java class as an entity.

It tells Hibernate:

> "This Java class represents a database table."

Normally, a Java class is just an object in memory. Hibernate does not know that this class should be stored in the database.

When you add:

```java
@Entity
```

Hibernate understands:

> "This class should be mapped to a database table."

---

## `@Table(value = "table_name")`

This annotation is generally placed after `@Entity`.

It is used to give a **custom name to the entity table**.

Example:

```java
@Entity
@Table(value = "students")
public class Student {
}
```

Here, the `Student` entity is mapped to the `students` database table.

---

## `@Id`

`@Id` is a JPA annotation used to identify the **Primary Key** of an entity.

It tells Hibernate:

> "This field uniquely identifies each record in the database table."

Example:

```java
@Id
private Long studentId;
```

Here, `studentId` is the primary key.

---

## `@GeneratedValue(strategy = GenerationType.AUTO)`

This is a JPA/Hibernate annotation used with the `@Id` field to tell Hibernate how the primary key should be generated automatically.

### `@GeneratedValue`

This annotation tells JPA:

> "The value of this primary key should be generated automatically."

Without it, you must provide the ID yourself.

### `strategy = GenerationType.AUTO`

This tells Hibernate:

> "Choose the appropriate ID generation strategy depending on the database and configuration."

Hibernate may use mechanisms such as:

* Identity columns
* Sequences
* Tables
* Other supported mechanisms

You generally do not need to worry about the underlying implementation when using `AUTO`.

Example:

```java
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long studentId;
```

---

## `@Column(nullable = false)`

This tells JPA that the corresponding database column should **not allow `NULL` values**.

It means:

> "This field is mandatory. Every record must have a value for this column."

Example:

```java
@Column(nullable = false)
private String studentName;
```

If a record is saved without a value for this column, the database constraint can reject the operation.

---

# 3. Configuration Class Annotations

## `@Configuration`

`@Configuration` is used to mark a class as a **configuration class**.

It tells Spring:

> "This class contains configuration (setup instructions) for the Spring application. Read this class and create/manage the beans defined inside it."

Example:

```java
@Configuration
public class AppConfig {

}
```

Configuration classes are commonly used to define beans using `@Bean`.

---

## `@EnableWebSecurity`

`@EnableWebSecurity` is a Spring Security annotation.

It tells Spring:

> "Enable Spring Security for this application and use my custom security configuration."

It is commonly used in a security configuration class where you configure things such as:

* Authentication
* Authorization
* Security filters
* Login configuration
* CSRF configuration
* Password encoding

Example:

```java
@EnableWebSecurity
@Configuration
public class SecurityConfig {

}
```

---

# 4. Global Exception Handling

## `@ControllerAdvice`

### Definition

`@ControllerAdvice` is a Spring annotation that creates a **global class whose methods can apply to all controllers in the application**.

Think of it as a **central control room for your controllers**.

Instead of writing the same exception-handling code in every controller, you write it once inside a class annotated with `@ControllerAdvice`.

### Flow

```text
Client
  |
  v
StudentController
  |
  v
Service
  |
  v
throws Exception
  |
  v
-----------------------
|  @ControllerAdvice  |
-----------------------
  |
  v
Returns Error Response
```

### Example

```java
@ControllerAdvice
public class GlobalExceptionHandler {

}
```

---

# 5. `@ExceptionHandler`

## Definition

`@ExceptionHandler` is a Spring annotation used to define a method that handles a **specific exception** thrown during request processing.

It allows you to handle exceptions gracefully and return a customized error response.

### Purpose

* Handle exceptions gracefully.
* Return meaningful error messages.
* Avoid writing `try-catch` blocks in every controller.
* Provide consistent HTTP responses to the client.
* Centralize exception-handling logic.

---

## How It Works

When an exception is thrown:

```text
Controller
    |
    v
Exception Thrown
    |
    v
Spring Searches for Matching @ExceptionHandler
    |
    v
Matching Method Executes
    |
    v
Custom Response Returned
```

---

## Basic Syntax

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<String> handleResourceNotFound(
        ResourceNotFoundException ex) {

    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ex.getMessage());
}
```

Here:

```java
@ExceptionHandler(ResourceNotFoundException.class)
```

means:

> "When `ResourceNotFoundException` occurs, execute this method."

---

# 6. Where Can `@ExceptionHandler` Be Used?

## 1. Inside a Controller

When placed inside a controller, it handles exceptions **only for that specific controller**.

Example:

```java
@RestController
public class StudentController {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleException(
            ResourceNotFoundException ex) {

        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }
}
```

---

## 2. Inside `@ControllerAdvice` — Recommended

When placed inside a class annotated with `@ControllerAdvice`, it can handle exceptions **globally across controllers**.

Example:

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleException(
            ResourceNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}
```

This approach is generally preferred for centralized exception handling.

---

# 7. Common Exceptions Handled

Examples include:

* `ResourceNotFoundException`
* `DuplicateResourceException`
* `MethodArgumentNotValidException`
* `IllegalArgumentException`
* `NullPointerException`
* `Exception` — Generic fallback

---

# 8. Advantages of `@ExceptionHandler`

* Centralized exception handling.
* Cleaner controller code.
* Consistent API error responses.
* Easy to maintain and extend.
* Useful for building industry-standard Spring Boot REST APIs.

---

# 9. Limitation of `@ExceptionHandler`

`@ExceptionHandler` primarily handles exceptions that occur during **controller/request processing**.

It does not normally handle exceptions that are thrown inside the **Spring Security Filter Chain**, such as:

* `CsrfException`
* `AuthenticationException`
* `AccessDeniedException`

These are generally handled by Spring Security components such as:

* `AuthenticationEntryPoint`
* `AccessDeniedHandler`

### Important Concept

```text
HTTP Request
     |
     v
Spring Security Filter Chain
     |
     |---- AuthenticationException
     |---- AccessDeniedException
     |---- CSRF-related exception
     |
     v
Controller
     |
     |---- Application Exception
     |
     v
@ControllerAdvice
     |
     v
@ExceptionHandler
```

---

# 10. `@ResponseStatus`

## Definition

`@ResponseStatus` is a Spring annotation used to specify the **HTTP status code** that should be returned in the response.

It can be applied to:

* Exception classes
* Controller methods

---

## Purpose

* Return a specific HTTP status code.
* Eliminate the need to manually create a `ResponseEntity` when only the status code is required.
* Make exception-to-status mapping simple and readable.

---

# 11. How `@ResponseStatus` Works

When Spring encounters an exception or executes a controller method annotated with `@ResponseStatus`, it automatically sets the specified HTTP status code in the response.

### Flow

```text
Client
  |
  v
Controller
  |
  v
Exception Thrown
  |
  v
@ResponseStatus Found
  |
  v
Spring Sets HTTP Status
  |
  v
Response Sent
```

---

# 12. `@ResponseStatus` on an Exception Class

Example:

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

Now, whenever this exception is thrown:

```java
throw new ResourceNotFoundException("Student not found");
```

Spring can return:

```text
HTTP/1.1 404 Not Found
```

---

# 13. `@ResponseStatus` on a Controller Method

Example:

```java
@PostMapping("/students")
@ResponseStatus(HttpStatus.CREATED)
public Student createStudent(@RequestBody Student student) {

    return service.save(student);
}
```

Spring returns:

```text
HTTP/1.1 201 Created
```

This is useful when the method should always return a particular status code.

---

# 14. Common HTTP Status Codes

| Status                             | Meaning                   |
| ---------------------------------- | ------------------------- |
| `HttpStatus.OK`                    | 200 OK                    |
| `HttpStatus.CREATED`               | 201 Created               |
| `HttpStatus.NO_CONTENT`            | 204 No Content            |
| `HttpStatus.BAD_REQUEST`           | 400 Bad Request           |
| `HttpStatus.UNAUTHORIZED`          | 401 Unauthorized          |
| `HttpStatus.FORBIDDEN`             | 403 Forbidden             |
| `HttpStatus.NOT_FOUND`             | 404 Not Found             |
| `HttpStatus.CONFLICT`              | 409 Conflict              |
| `HttpStatus.INTERNAL_SERVER_ERROR` | 500 Internal Server Error |

---

# 15. `@ResponseStatus` vs `ResponseEntity`

## Using `@ResponseStatus`

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
}
```

### Result

Spring automatically uses:

```text
404 Not Found
```

---

## Using `ResponseEntity`

```java
return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body("Student not found");
```

### Result

You can control both:

* HTTP status code
* Response body

For example:

```text
Status: 404 Not Found

Body:
Student not found
```

---

# 16. When to Use `@ResponseStatus`

Use `@ResponseStatus` when:

* Only the HTTP status needs to be specified.
* The status is fixed.
* Creating custom exception classes with a fixed status code.
* You want a simple and concise solution.

Example:

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class StudentNotFoundException extends RuntimeException {
}
```

---

# 17. When Not to Use `@ResponseStatus`

Avoid relying only on `@ResponseStatus` when:

* You need a custom JSON error response.
* You want to include timestamps.
* You need error codes.
* You need the request path.
* You need validation details.
* You need dynamic status codes.

In these cases, prefer:

```java
@ExceptionHandler
```

together with:

```java
ResponseEntity
```

---

# 18. Advantages of `@ResponseStatus`

* Simple and concise.
* Automatically maps exceptions to HTTP status codes.
* Reduces boilerplate code.
* Improves readability.
* Useful for exceptions with a fixed HTTP status.

---

# 19. Limitations of `@ResponseStatus`

* Returns a fixed HTTP status.
* Less flexible than `ResponseEntity`.
* Not suitable for complex custom error responses.
* Cannot easily provide dynamic response information.

For detailed API error responses, `ResponseEntity` is usually more flexible.

---

# 20. Interview Definitions

## `@SpringBootApplication`

> `@SpringBootApplication` is the main Spring Boot annotation that combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan` to configure and bootstrap a Spring Boot application.

---

## `@Entity`

> `@Entity` is a JPA annotation that tells Hibernate that a Java class should be mapped to a database table.

---

## `@Table`

> `@Table` is a JPA annotation used to specify the database table name to which an entity is mapped.

---

## `@Id`

> `@Id` identifies the primary key of a JPA entity.

---

## `@GeneratedValue`

> `@GeneratedValue` tells JPA that the primary key value should be generated automatically.

---

## `@Column(nullable = false)`

> `@Column(nullable = false)` specifies that the corresponding database column should not allow null values.

---

## `@Configuration`

> `@Configuration` tells Spring that a class contains configuration and bean definitions for the application.

---

## `@EnableWebSecurity`

> `@EnableWebSecurity` enables Spring Security's web security configuration and allows custom security configuration to be applied.

---

## `@ControllerAdvice`

> `@ControllerAdvice` provides centralized exception handling and other controller-related functionality across multiple controllers.

---

## `@ExceptionHandler`

> `@ExceptionHandler` defines a method that handles a specific exception thrown during request processing.

---

## `@ResponseStatus`

> `@ResponseStatus` specifies the HTTP status code that Spring should return for a controller method or exception.

---

# 21. Quick Revision

```text
@SpringBootApplication
        |
        +---- @Configuration
        |
        +---- @EnableAutoConfiguration
        |
        +---- @ComponentScan


@Entity
        |
        +---- Java class → Database Table

@Table
        |
        +---- Custom Table Name

@Id
        |
        +---- Primary Key

@GeneratedValue
        |
        +---- Automatically Generate ID

@Column(nullable = false)
        |
        +---- Column Cannot Be NULL


@Configuration
        |
        +---- Configuration / Bean Setup

@EnableWebSecurity
        |
        +---- Enable Spring Security


@ControllerAdvice
        |
        +---- Global Controller-Level Exception Handling

@ExceptionHandler
        |
        +---- Handle Specific Exceptions

@ResponseStatus
        |
        +---- Set HTTP Status Code
```

## Key Difference to Remember

```text
@ControllerAdvice
        ↓
Global exception-handling class

@ExceptionHandler
        ↓
Handles a specific exception

@ResponseStatus
        ↓
Specifies the HTTP status code

ResponseEntity
        ↓
Controls status + headers + response body
```

### One-Line Summary

> `@SpringBootApplication` starts and configures the application, entity annotations map Java objects to database tables, configuration/security annotations configure the application infrastructure, and exception-handling annotations manage errors and HTTP responses.
