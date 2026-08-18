# Spring MVC with Spring Boot — Complete Project Flow & Notes

> A complete beginner-to-intermediate guide to understanding **Spring MVC architecture**, its project layers, request flow, annotations, DTOs, validation, Thymeleaf, and how all components work together.

---

# 1. What is Spring MVC?

**Spring MVC** is a web framework provided by the Spring Framework for building web applications using the **Model–View–Controller (MVC)** design pattern.

MVC separates an application into three major responsibilities:

```text
Model       → Represents application data
View        → Displays data to the user
Controller  → Handles user requests and controls the flow
```

In a typical Spring Boot MVC project, this architecture is expanded into multiple layers:

```text
                    CLIENT / BROWSER
                           |
                           | HTTP Request
                           v
                    +--------------+
                    |  Controller  |
                    +--------------+
                           |
                           v
                    +--------------+
                    |    Service   |
                    +--------------+
                           |
                           v
                    +--------------+
                    |  Repository  |
                    +--------------+
                           |
                           v
                    +--------------+
                    |   Database   |
                    +--------------+
                           |
                           | Data
                           v
                    +--------------+
                    |    Service   |
                    +--------------+
                           |
                           v
                    +--------------+
                    |  Controller  |
                    +--------------+
                           |
                           v
                    +--------------+
                    |  Thymeleaf   |
                    |     View     |
                    +--------------+
                           |
                           v
                       Browser
```

---

# 2. Complete Spring Boot MVC Project Flow

The project flow can be understood in these steps:

```text
1. Entity / Model
        ↓
2. Repository
        ↓
3. DTO + Validation
        ↓
4. Service Interface
        ↓
5. Service Implementation
        ↓
6. Controller
        ↓
7. Thymeleaf Views
```

A more complete request flow is:

```text
User
 ↓
Browser
 ↓
HTTP Request
 ↓
Controller
 ↓
Service
 ↓
Repository
 ↓
Database
 ↓
Repository
 ↓
Service
 ↓
Controller
 ↓
Model
 ↓
Thymeleaf
 ↓
HTML Response
 ↓
Browser
```

---

# 3. Spring Boot Project Structure

A typical Spring MVC project can look like this:

```text
src/
└── main/
    ├── java/
    │   └── com.example.studentmanagement/
    │
    │       ├── StudentManagementApplication.java
    │       │
    │       ├── controller/
    │       │   └── StudentController.java
    │       │
    │       ├── service/
    │       │   ├── StudentService.java
    │       │   └── StudentServiceImpl.java
    │       │
    │       ├── repository/
    │       │   └── StudentRepository.java
    │       │
    │       ├── entity/
    │       │   └── Student.java
    │       │
    │       ├── dto/
    │       │   └── StudentDTO.java
    │       │
    │       └── exception/
    │           └── GlobalExceptionHandler.java
    │
    └── resources/
        ├── templates/
        │   ├── students.html
        │   ├── student-form.html
        │   └── student-details.html
        │
        ├── static/
        │   ├── css/
        │   ├── js/
        │   └── images/
        │
        └── application.properties
```

---

# 4. Layer 1 — Entity / Model

## What is an Entity?

An **Entity** represents data that will normally be stored in the database.

For example, in a Student Management System:

```text
Student
 ├── id
 ├── name
 ├── email
 ├── age
 └── course
```

The entity maps Java objects to database tables using **JPA/Hibernate**.

---

## Example

```java
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private int age;

    private String course;

    // Constructors
    // Getters
    // Setters
}
```

---

## Important Annotations

### `@Entity`

```java
@Entity
```

Tells JPA:

> "This Java class represents a database entity."

---

### `@Table`

```java
@Table(name = "students")
```

Specifies the database table associated with the entity.

---

### `@Id`

```java
@Id
```

Marks the primary key.

---

### `@GeneratedValue`

```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```

Allows the database to automatically generate the ID.

---

## Entity Responsibility

The Entity should primarily represent **database data and relationships**.

```text
Entity
  ↓
Java Object
  ↓
Database Table
```

Example:

```text
Student Object

id = 101
name = "Rahul"
email = "rahul@gmail.com"
age = 21
course = "Java"
```

can correspond to:

```text
students table

+-----+--------+------------------+-----+--------+
| id  | name   | email            | age | course |
+-----+--------+------------------+-----+--------+
| 101 | Rahul  | rahul@gmail.com  | 21  | Java   |
+-----+--------+------------------+-----+--------+
```

---

# 5. Layer 2 — Repository

The **Repository layer** communicates with the database.

Spring Data JPA makes database operations much easier.

---

## Example

```java
@Repository
public interface StudentRepository
        extends JpaRepository<Student, Long> {

}
```

Here:

```text
Student
   ↑
Entity type

Long
   ↑
Primary key type
```

---

# 6. Why JpaRepository?

`JpaRepository` provides many ready-made database operations.

For example:

```java
save()
findById()
findAll()
deleteById()
count()
existsById()
```

So instead of writing SQL manually:

```sql
SELECT * FROM students;
```

you can write:

```java
studentRepository.findAll();
```

---

# 7. Repository Responsibilities

Repository should handle **data access**.

```text
Service
   ↓
Repository
   ↓
Database
```

The repository should NOT contain business logic.

For example:

```java
studentRepository.findAll();
```

is data access.

But:

```java
if (student.getAge() >= 18) {
    // complicated business rule
}
```

should normally belong in the service layer.

---

# 8. Derived Query Methods

Spring Data JPA can create queries based on method names.

Example:

```java
Optional<Student> findByEmail(String email);
```

Spring understands:

```text
find + By + Email
```

and generates the appropriate query.

Another example:

```java
List<Student> findByCourse(String course);
```

You can also use:

```java
List<Student> findByNameContaining(String name);
```

This can be useful for search functionality.

---

# 9. Layer 3 — DTO

DTO means:

> **Data Transfer Object**

A DTO is an object used to transfer data between layers or between the client and application.

---

# 10. Why Do We Need DTOs?

Suppose your entity contains:

```text
Student Entity

id
name
email
password
createdAt
updatedAt
internalData
```

You may not want to expose all these fields to the browser.

A DTO allows you to control exactly what data is transferred.

---

## Example DTO

```java
public class StudentDTO {

    private String name;

    private String email;

    private Integer age;

    private String course;

    // Getters and setters
}
```

Now the controller can work with:

```text
StudentDTO
```

instead of directly exposing:

```text
Student Entity
```

---

# 11. Entity vs DTO

| Entity                                   | DTO                                   |
| ---------------------------------------- | ------------------------------------- |
| Represents database data                 | Represents transferred data           |
| Used by JPA/Hibernate                    | Used for data transfer                |
| Maps to database                         | Does not necessarily map to database  |
| Contains persistence-related information | Contains API/form-related information |
| Can contain internal fields              | Can expose only required fields       |

---

# 12. DTO Validation

DTOs are commonly used together with Bean Validation.

Example:

```java
public class StudentDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;

    @NotBlank(message = "Course is required")
    private String course;
}
```

---

# 13. Common Validation Annotations

### `@NotBlank`

```java
@NotBlank
```

Ensures that a String is not:

```text
null
""
"   "
```

---

### `@NotNull`

```java
@NotNull
```

Ensures that the value is not `null`.

---

### `@Email`

```java
@Email
```

Checks whether a String has a valid email format.

---

### `@Size`

```java
@Size(min = 3, max = 50)
```

Restricts the size of a String or collection.

---

### `@Min`

```java
@Min(18)
```

Specifies the minimum numeric value.

---

### `@Max`

```java
@Max(100)
```

Specifies the maximum numeric value.

---

# 14. `@Valid`

Validation is triggered using:

```java
@Valid
```

Example:

```java
@PostMapping("/students")
public String saveStudent(
        @Valid @ModelAttribute StudentDTO studentDTO,
        BindingResult result) {

    if (result.hasErrors()) {
        return "student-form";
    }

    studentService.saveStudent(studentDTO);

    return "redirect:/students";
}
```

The important part is:

```java
@Valid
```

It tells Spring:

> "Validate this object's fields using the validation annotations."

---

# 15. `BindingResult`

```java
BindingResult result
```

contains validation errors.

For example:

```text
Name is required
Invalid email
Age must be at least 18
```

You can check:

```java
if (result.hasErrors()) {
    return "student-form";
}
```

---

# 16. Layer 4 — Service Interface

The Service layer contains the **business logic** of the application.

A common industry approach is:

```text
Service Interface
       ↓
Service Implementation
```

Example:

```java
public interface StudentService {

    List<StudentDTO> getAllStudents();

    StudentDTO getStudentById(Long id);

    void saveStudent(StudentDTO studentDTO);

    void updateStudent(Long id, StudentDTO studentDTO);

    void deleteStudent(Long id);
}
```

---

# 17. Why Create a Service Interface?

It provides abstraction.

The controller does not need to know how the business logic is implemented.

```text
Controller
    ↓
StudentService
    ↓
StudentServiceImpl
```

The controller only knows:

```java
studentService.saveStudent(studentDTO);
```

It does not need to know every internal operation.

---

# 18. Layer 5 — Service Implementation

The actual business logic is implemented here.

Example:

```java
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void saveStudent(StudentDTO studentDTO) {

        Student student = new Student();

        student.setName(studentDTO.getName());
        student.setEmail(studentDTO.getEmail());
        student.setAge(studentDTO.getAge());
        student.setCourse(studentDTO.getCourse());

        studentRepository.save(student);
    }
}
```

---

# 19. What Does `@Service` Do?

```java
@Service
```

tells Spring:

> "Create and manage an object of this class as a Spring Bean."

Spring can then inject it into the controller.

```text
Spring Container
      |
      +---- StudentController
      |
      +---- StudentServiceImpl
      |
      +---- StudentRepository
```

---

# 20. Dependency Injection

Instead of manually creating objects:

```java
StudentService service = new StudentServiceImpl();
```

Spring manages the object.

Example:

```java
private final StudentService studentService;

public StudentController(StudentService studentService) {
    this.studentService = studentService;
}
```

Spring automatically provides the required dependency.

This is called:

> **Dependency Injection (DI)**

---

# 21. Why Constructor Injection?

Preferred approach:

```java
public StudentController(StudentService studentService) {
    this.studentService = studentService;
}
```

Advantages:

* Dependencies are explicit.
* Easier to test.
* Supports immutable fields.
* Recommended for required dependencies.

---

# 22. Layer 6 — Controller

The Controller handles HTTP requests.

Example:

```java
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public String getStudents(Model model) {

        List<StudentDTO> students =
                studentService.getAllStudents();

        model.addAttribute("students", students);

        return "students";
    }
}
```

---

# 23. What Does `@Controller` Mean?

```java
@Controller
```

tells Spring:

> "This class handles web requests and can return views."

For a Thymeleaf MVC application, `@Controller` is normally used.

---

# 24. `@RestController` vs `@Controller`

### `@Controller`

Used mainly when returning HTML views.

```java
@Controller
public class StudentController {
}
```

Example:

```java
return "students";
```

means:

```text
Find students.html
```

---

### `@RestController`

Used mainly for REST APIs.

```java
@RestController
public class StudentRestController {
}
```

Example:

```java
return student;
```

returns data such as:

```json
{
    "id": 1,
    "name": "Rahul"
}
```

---

# 25. `@RequestMapping`

```java
@RequestMapping("/students")
```

defines a common URL prefix.

Example:

```java
@Controller
@RequestMapping("/students")
public class StudentController {
}
```

Then:

```java
@GetMapping
```

maps to:

```text
GET /students
```

And:

```java
@GetMapping("/new")
```

maps to:

```text
GET /students/new
```

---

# 26. HTTP Mapping Annotations

### GET

```java
@GetMapping
```

Used for retrieving/displaying data.

---

### POST

```java
@PostMapping
```

Used for submitting/creating data.

---

### PUT

```java
@PutMapping
```

Usually used for updating data in REST APIs.

---

### DELETE

```java
@DeleteMapping
```

Used for deleting data.

---

# 27. Example CRUD Controller

```java
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public String listStudents(Model model) {

        model.addAttribute(
                "students",
                studentService.getAllStudents()
        );

        return "students";
    }

    @GetMapping("/new")
    public String showStudentForm(Model model) {

        model.addAttribute("student", new StudentDTO());

        return "student-form";
    }

    @PostMapping("/save")
    public String saveStudent(
            @Valid @ModelAttribute("student")
            StudentDTO studentDTO,
            BindingResult result) {

        if (result.hasErrors()) {
            return "student-form";
        }

        studentService.saveStudent(studentDTO);

        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {

        studentService.deleteStudent(id);

        return "redirect:/students";
    }
}
```

---

# 28. `Model`

The `Model` is used to send data from the controller to the view.

Example:

```java
model.addAttribute("students", students);
```

Now the Thymeleaf page can access:

```text
students
```

---

# 29. Controller → View Flow

Suppose:

```java
model.addAttribute("name", "Bhuvanesh");

return "home";
```

Spring looks for:

```text
templates/home.html
```

Thymeleaf can then use:

```html
<h1 th:text="${name}"></h1>
```

The browser receives:

```html
<h1>Bhuvanesh</h1>
```

---

# 30. `@ModelAttribute`

Used to bind form data to an object.

Example:

```java
@PostMapping("/save")
public String saveStudent(
        @ModelAttribute StudentDTO studentDTO) {

    studentService.saveStudent(studentDTO);

    return "redirect:/students";
}
```

If the HTML form contains:

```html
<input name="name">
<input name="email">
<input name="age">
```

Spring maps the values to:

```java
StudentDTO
```

---

# 31. `@PathVariable`

Used to extract values from the URL.

Example:

```java
@GetMapping("/students/{id}")
public String getStudent(@PathVariable Long id) {
    ...
}
```

URL:

```text
/students/101
```

Then:

```java
id = 101
```

---

# 32. `@RequestParam`

Used to read query parameters.

Example:

```java
@GetMapping("/search")
public String search(
        @RequestParam String name) {
    ...
}
```

URL:

```text
/students/search?name=Rahul
```

Then:

```text
name = Rahul
```

---

# 33. Layer 7 — Thymeleaf

**Thymeleaf** is a server-side Java template engine commonly used with Spring MVC.

It generates HTML dynamically.

Project structure:

```text
src/main/resources/templates/
```

Example:

```text
templates/
├── home.html
├── students.html
├── student-form.html
└── student-details.html
```

---

# 34. Basic Thymeleaf Example

Controller:

```java
model.addAttribute("student", student);

return "student-details";
```

HTML:

```html
<h1 th:text="${student.name}"></h1>

<p th:text="${student.email}"></p>

<p th:text="${student.course}"></p>
```

---

# 35. Thymeleaf Iteration

Suppose controller sends:

```java
model.addAttribute("students", students);
```

Thymeleaf:

```html
<table>

    <tr th:each="student : ${students}">

        <td th:text="${student.id}"></td>

        <td th:text="${student.name}"></td>

        <td th:text="${student.email}"></td>

        <td th:text="${student.course}"></td>

    </tr>

</table>
```

`th:each` loops through the collection.

---

# 36. Thymeleaf Conditional Rendering

```html
<p th:if="${student.age >= 18}">
    Adult Student
</p>
```

Another example:

```html
<p th:unless="${student.age >= 18}">
    Minor Student
</p>
```

---

# 37. Thymeleaf Form

Example:

```html
<form
    th:action="@{/students/save}"
    th:object="${student}"
    method="post">

    <input
        type="text"
        th:field="*{name}">

    <input
        type="email"
        th:field="*{email}">

    <input
        type="number"
        th:field="*{age}">

    <input
        type="text"
        th:field="*{course}">

    <button type="submit">
        Save
    </button>

</form>
```

---

# 38. Displaying Validation Errors

Example:

```html
<input
    type="text"
    th:field="*{name}">

<p
    th:if="${#fields.hasErrors('name')}"
    th:errors="*{name}">
</p>
```

This displays validation errors generated by:

```java
@NotBlank
```

---

# 39. Complete Form Submission Flow

Consider:

```text
User fills form
       ↓
Clicks Submit
       ↓
POST /students/save
       ↓
Controller
       ↓
@Valid
       ↓
Validation
       ↓
Validation successful?
       ↓
      YES
       ↓
Service
       ↓
Repository
       ↓
Database
       ↓
redirect:/students
       ↓
GET /students
       ↓
Controller
       ↓
Service
       ↓
Repository
       ↓
Database
       ↓
Model
       ↓
Thymeleaf
       ↓
HTML
       ↓
Browser
```

If validation fails:

```text
POST /students/save
       ↓
Controller
       ↓
@Valid
       ↓
Validation
       ↓
Errors found
       ↓
BindingResult
       ↓
student-form.html
       ↓
Display errors
```

---

# 40. Complete Example

## Entity

```java
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private Integer age;
    private String course;

    // getters and setters
}
```

---

## Repository

```java
@Repository
public interface StudentRepository
        extends JpaRepository<Student, Long> {

}
```

---

## DTO

```java
public class StudentDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;

    @NotBlank(message = "Course is required")
    private String course;

    // getters and setters
}
```

---

## Service

```java
public interface StudentService {

    List<StudentDTO> getAllStudents();

    void saveStudent(StudentDTO studentDTO);

    void deleteStudent(Long id);
}
```

---

## Service Implementation

```java
@Service
public class StudentServiceImpl
        implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(
            StudentRepository studentRepository) {

        this.studentRepository = studentRepository;
    }

    @Override
    public List<StudentDTO> getAllStudents() {

        return studentRepository.findAll()
                .stream()
                .map(student -> {

                    StudentDTO dto = new StudentDTO();

                    dto.setName(student.getName());
                    dto.setEmail(student.getEmail());
                    dto.setAge(student.getAge());
                    dto.setCourse(student.getCourse());

                    return dto;

                })
                .toList();
    }

    @Override
    public void saveStudent(StudentDTO dto) {

        Student student = new Student();

        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setAge(dto.getAge());
        student.setCourse(dto.getCourse());

        studentRepository.save(student);
    }

    @Override
    public void deleteStudent(Long id) {

        studentRepository.deleteById(id);
    }
}
```

---

## Controller

```java
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(
            StudentService studentService) {

        this.studentService = studentService;
    }

    @GetMapping
    public String listStudents(Model model) {

        model.addAttribute(
                "students",
                studentService.getAllStudents()
        );

        return "students";
    }

    @GetMapping("/new")
    public String showForm(Model model) {

        model.addAttribute(
                "student",
                new StudentDTO()
        );

        return "student-form";
    }

    @PostMapping("/save")
    public String saveStudent(
            @Valid
            @ModelAttribute("student")
            StudentDTO studentDTO,
            BindingResult result) {

        if (result.hasErrors()) {
            return "student-form";
        }

        studentService.saveStudent(studentDTO);

        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(
            @PathVariable Long id) {

        studentService.deleteStudent(id);

        return "redirect:/students";
    }
}
```

---

# 41. Complete Architecture Diagram

```text
                         USER
                           |
                           v
                       BROWSER
                           |
                           | HTTP Request
                           v
                +----------------------+
                |      CONTROLLER      |
                |   @Controller        |
                |   @GetMapping        |
                |   @PostMapping       |
                +----------------------+
                           |
                           | calls
                           v
                +----------------------+
                |       SERVICE        |
                |   Business Logic     |
                +----------------------+
                           |
                           v
                +----------------------+
                |  SERVICE IMPLEMENT.  |
                |      @Service        |
                +----------------------+
                           |
                           v
                +----------------------+
                |     REPOSITORY       |
                |   JpaRepository      |
                +----------------------+
                           |
                           v
                +----------------------+
                |      DATABASE        |
                |      PostgreSQL      |
                |       MySQL          |
                +----------------------+
                           |
                           v
                    Data returned
                           |
                           v
                +----------------------+
                |       SERVICE        |
                +----------------------+
                           |
                           v
                +----------------------+
                |      CONTROLLER      |
                +----------------------+
                           |
                           | Model
                           v
                +----------------------+
                |      THYMELEAF       |
                |        VIEW          |
                +----------------------+
                           |
                           v
                       HTML
                           |
                           v
                       BROWSER
```

---

# 42. Why Use Layers?

Layered architecture provides **separation of concerns**.

Without layers:

```text
Controller
    |
    +-- Database code
    +-- Business logic
    +-- Validation
    +-- HTML handling
    +-- Everything mixed together
```

This becomes difficult to maintain.

With layers:

```text
Controller
    ↓
Handles HTTP

Service
    ↓
Handles business logic

Repository
    ↓
Handles database operations

Entity
    ↓
Represents database data

DTO
    ↓
Transfers/validates data

Thymeleaf
    ↓
Displays data
```

This makes the application easier to:

* Maintain
* Test
* Debug
* Scale
* Modify
* Understand

---

# 43. Spring MVC Request Lifecycle

One of the most important concepts is understanding what happens when a request enters a Spring MVC application.

Suppose the user opens:

```text
http://localhost:8080/students
```

The flow is:

```text
Browser
   ↓
HTTP GET /students
   ↓
DispatcherServlet
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
   ↓
Repository
   ↓
Service
   ↓
Controller
   ↓
Model
   ↓
View Resolver
   ↓
Thymeleaf
   ↓
HTML
   ↓
Browser
```

---

# 44. DispatcherServlet

`DispatcherServlet` is a central component of Spring MVC.

It acts as the **front controller**.

Conceptually:

```text
                HTTP Request
                      |
                      v
              DispatcherServlet
                      |
          +-----------+-----------+
          |           |           |
          v           v           v
      Controller   Controller   Controller
```

It receives incoming requests and determines which controller should handle them.

In Spring Boot, much of this setup is automatically configured for you.

---

# 45. Example of DispatcherServlet Flow

Request:

```text
GET /students
```

Spring identifies the appropriate controller method:

```java
@GetMapping("/students")
public String listStudents(Model model) {
    ...
}
```

Then the controller executes.

---

# 46. View Resolution

Suppose the controller returns:

```java
return "students";
```

Spring interprets this as a logical view name.

With Thymeleaf configured, it looks for something like:

```text
templates/students.html
```

Then Thymeleaf processes the template.

---

# 47. Redirect vs Forward

## Forward

```java
return "student-form";
```

This directly renders:

```text
student-form.html
```

---

## Redirect

```java
return "redirect:/students";
```

This tells the browser to make a new request:

```text
GET /students
```

---

# 48. Why Use Redirect After POST?

A common pattern is:

```text
POST
 ↓
Save data
 ↓
Redirect
 ↓
GET
```

This is called:

> **Post/Redirect/Get (PRG)**

Example:

```java
@PostMapping("/save")
public String saveStudent(...) {

    studentService.saveStudent(studentDTO);

    return "redirect:/students";
}
```

This helps prevent accidental duplicate form submission when the user refreshes the page.

---

# 49. Common Spring MVC Annotations

| Annotation          | Purpose                     |
| ------------------- | --------------------------- |
| `@Controller`       | Defines MVC controller      |
| `@RestController`   | Defines REST controller     |
| `@RequestMapping`   | Maps common URL             |
| `@GetMapping`       | Handles GET                 |
| `@PostMapping`      | Handles POST                |
| `@PutMapping`       | Handles PUT                 |
| `@DeleteMapping`    | Handles DELETE              |
| `@PathVariable`     | Reads URL variable          |
| `@RequestParam`     | Reads query parameter       |
| `@ModelAttribute`   | Binds form data             |
| `@Valid`            | Performs validation         |
| `@Service`          | Defines service bean        |
| `@Repository`       | Defines repository          |
| `@Entity`           | Defines JPA entity          |
| `@Id`               | Defines primary key         |
| `@GeneratedValue`   | Generates primary key       |
| `@ControllerAdvice` | Global exception handling   |
| `@ExceptionHandler` | Handles specific exceptions |

---

# 50. Global Exception Handling

A professional Spring MVC application should handle exceptions centrally.

Example:

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public String handleStudentNotFound(
            StudentNotFoundException ex,
            Model model) {

        model.addAttribute("error", ex.getMessage());

        return "error";
    }
}
```

This avoids putting exception handling logic into every controller.

---

# 51. Typical Exception Flow

```text
Controller
    ↓
Service
    ↓
Student not found
    ↓
Exception thrown
    ↓
@ControllerAdvice
    ↓
@ExceptionHandler
    ↓
Error View
    ↓
Browser
```

---

# 52. Spring MVC + Spring Security

When Spring Security is added, the flow becomes:

```text
Browser
   ↓
Security Filter Chain
   ↓
Authentication
   ↓
Authorization
   ↓
DispatcherServlet
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

Security therefore works before the request reaches the controller.

For example:

```text
GET /students
       ↓
Is user authenticated?
       |
   +---+---+
   |       |
  YES      NO
   |       |
   v       v
Controller Login
```

---

# 53. Static Resources

Spring MVC applications commonly contain:

```text
src/main/resources/static/
```

Example:

```text
static/
├── css/
│   └── style.css
├── js/
│   └── script.js
└── images/
    └── logo.png
```

These resources are served directly to the browser.

---

# 54. `application.properties`

Spring MVC applications commonly contain configuration such as:

```properties
spring.application.name=student-management

spring.datasource.url=jdbc:mysql://localhost:3306/student_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.thymeleaf.cache=false
```

For production applications, sensitive credentials should not be hard-coded into source code.

---

# 55. Maven Dependencies

A typical Spring MVC + Thymeleaf + JPA application may use:

```xml
<dependencies>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>
            org.springframework.boot
        </groupId>
        <artifactId>
            spring-boot-starter-thymeleaf
        </artifactId>
    </dependency>

    <dependency>
        <groupId>
            org.springframework.boot
        </groupId>
        <artifactId>
            spring-boot-starter-data-jpa
        </artifactId>
    </dependency>

    <dependency>
        <groupId>
            org.springframework.boot
        </groupId>
        <artifactId>
            spring-boot-starter-validation
        </artifactId>
    </dependency>

</dependencies>
```

For a real project, add the database driver appropriate to your database.

---

# 56. Full CRUD Flow

A Student Management System usually contains:

```text
CREATE
  ↓
POST /students/save

READ
  ↓
GET /students

UPDATE
  ↓
GET /students/edit/{id}
POST /students/update/{id}

DELETE
  ↓
GET/POST /students/delete/{id}
```

A REST API would typically use:

```text
POST    /students
GET     /students
GET     /students/{id}
PUT     /students/{id}
DELETE  /students/{id}
```

---

# 57. Example CRUD Architecture

```text
                    STUDENT MANAGEMENT SYSTEM

                            Browser
                               |
                +--------------+--------------+
                |              |              |
              Create          Read          Delete
                |              |              |
                +--------------+--------------+
                               |
                               v
                         Controller
                               |
                               v
                           Service
                               |
                               v
                         Repository
                               |
                               v
                           Database
                               |
                               v
                         Repository
                               |
                               v
                           Service
                               |
                               v
                         Controller
                               |
                               v
                           Model
                               |
                               v
                           Thymeleaf
                               |
                               v
                           Browser
```

---

# 58. Most Important Concept — Who Does What?

Remember this table:

| Layer                  | Main Responsibility                  |
| ---------------------- | ------------------------------------ |
| Entity                 | Represents database data             |
| DTO                    | Transfers and validates data         |
| Repository             | Communicates with database           |
| Service Interface      | Defines business operations          |
| Service Implementation | Implements business logic            |
| Controller             | Handles HTTP requests                |
| Model                  | Transfers data to view               |
| Thymeleaf              | Generates HTML                       |
| Database               | Stores persistent data               |
| Exception Handler      | Handles errors centrally             |
| Security               | Handles authentication/authorization |

---

# 59. What Should NOT Be Done?

## Don't put database logic in Controller

Bad:

```java
@Controller
public class StudentController {

    // SQL/database operations directly here
}
```

Better:

```text
Controller
    ↓
Service
    ↓
Repository
```

---

## Don't put business logic in Repository

Repository should focus on data access.

Bad:

```java
studentRepository.calculateComplexBusinessRule();
```

Better:

```text
Service
   ↓
Business Rule
   ↓
Repository
```

---

## Don't expose sensitive entity fields unnecessarily

Instead use DTOs.

```text
Entity
   ↓
DTO
   ↓
Controller
   ↓
View
```

---

# 60. The Golden Rule of Spring MVC

The easiest way to remember the architecture is:

```text
CONTROLLER
"What request did the user send?"

        ↓

SERVICE
"What should the application do?"

        ↓

REPOSITORY
"How do I get/save the data?"

        ↓

DATABASE
"Where is the data stored?"

        ↓

REPOSITORY
"Here is the data."

        ↓

SERVICE
"Here is the processed result."

        ↓

CONTROLLER
"Send this to the view."

        ↓

THYMELEAF
"Turn the data into HTML."

        ↓

BROWSER
"Display the page."
```

---

# 61. One Complete Example in Simple Words

Suppose a user clicks:

```text
Add Student
```

The browser opens:

```text
/students/new
```

### Step 1 — Controller

Controller receives:

```text
GET /students/new
```

and returns:

```java
return "student-form";
```

---

### Step 2 — Thymeleaf

Spring loads:

```text
student-form.html
```

The user enters:

```text
Name: Rahul
Email: rahul@gmail.com
Age: 21
Course: Java
```

---

### Step 3 — Submit

Browser sends:

```text
POST /students/save
```

---

### Step 4 — Controller

Controller receives the form:

```java
@ModelAttribute StudentDTO studentDTO
```

---

### Step 5 — Validation

Spring executes:

```java
@Valid
```

and checks:

```text
Name       → @NotBlank
Email      → @Email
Age        → @Min
Course     → @NotBlank
```

---

### Step 6 — Service

If validation succeeds:

```java
studentService.saveStudent(studentDTO);
```

---

### Step 7 — Repository

Service creates/updates the entity and calls:

```java
studentRepository.save(student);
```

---

### Step 8 — Database

The student is stored.

```text
students
----------------------------------
id | name  | email           | age
----------------------------------
1  | Rahul | rahul@gmail.com | 21
```

---

### Step 9 — Redirect

Controller:

```java
return "redirect:/students";
```

---

### Step 10 — Read Students

Browser makes:

```text
GET /students
```

Controller asks service:

```java
studentService.getAllStudents();
```

Service asks repository:

```java
studentRepository.findAll();
```

---

### Step 11 — Thymeleaf

The data is placed into the model:

```java
model.addAttribute("students", students);
```

Thymeleaf displays the students.

---

# 62. Final Spring MVC Architecture

```text
                         ┌─────────────────┐
                         │      USER       │
                         └────────┬────────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │    BROWSER      │
                         └────────┬────────┘
                                  │
                              HTTP Request
                                  │
                                  ▼
                    ┌──────────────────────────┐
                    │    Spring Security      │
                    │     Filter Chain        │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                    ┌──────────────────────────┐
                    │     DispatcherServlet    │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                    ┌──────────────────────────┐
                    │       Controller         │
                    │       @Controller        │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                    ┌──────────────────────────┐
                    │         DTO              │
                    │   Validation @Valid      │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                    ┌──────────────────────────┐
                    │         Service          │
                    │      Business Logic      │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                    ┌──────────────────────────┐
                    │       Repository         │
                    │      JpaRepository       │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                    ┌──────────────────────────┐
                    │        Entity            │
                    │       JPA/Hibernate      │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                    ┌──────────────────────────┐
                    │        DATABASE          │
                    └────────────┬─────────────┘
                                 │
                              Data
                                 │
                                 ▼
                    ┌──────────────────────────┐
                    │       Repository         │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                    ┌──────────────────────────┐
                    │         Service          │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                    ┌──────────────────────────┐
                    │       Controller         │
                    │         Model            │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                    ┌──────────────────────────┐
                    │        Thymeleaf         │
                    │          View             │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                         ┌───────────────┐
                         │     HTML      │
                         └───────┬───────┘
                                 │
                                 ▼
                         ┌───────────────┐
                         │    BROWSER    │
                         └───────────────┘
```

---

# 63. Quick Revision

```text
Entity
  → Database representation

Repository
  → Database operations

DTO
  → Data transfer + validation

Service
  → Business logic

Controller
  → HTTP request handling

Model
  → Sends data to View

Thymeleaf
  → Generates HTML

DispatcherServlet
  → Central request dispatcher

@ControllerAdvice
  → Global exception handling

Spring Security
  → Authentication + Authorization
```

---

# 64. Interview-Level Summary

If an interviewer asks:

### "Explain Spring MVC architecture."

A good answer is:

> Spring MVC is based on the Model-View-Controller design pattern. In a typical Spring Boot MVC application, the Controller handles HTTP requests, the Service layer contains business logic, and the Repository layer communicates with the database through Spring Data JPA. Entities represent persistent database data, while DTOs are used to transfer and validate data. The Controller adds the required data to the Model and returns a logical view name. Thymeleaf then processes the view and generates the final HTML response for the browser. Spring MVC uses the DispatcherServlet as the front controller to route incoming requests to the appropriate controller.

---

# 65. One-Line Memory Trick

Remember:

```text
BROWSER
   ↓
CONTROLLER
   ↓
SERVICE
   ↓
REPOSITORY
   ↓
DATABASE
   ↓
REPOSITORY
   ↓
SERVICE
   ↓
CONTROLLER
   ↓
THYMELEAF
   ↓
BROWSER
```

And remember the responsibility:

```text
Entity     → Data
DTO        → Transfer + Validation
Repository → Database
Service    → Business Logic
Controller → Request
Thymeleaf  → UI
```

This is the core **Spring Boot MVC project flow** you should understand before building a real Student Management System.
