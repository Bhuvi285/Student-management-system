# How Spring MVC Resolves `login.html` from `return "login"` in Controller?

Yes. In your code:

```java
@GetMapping("/login")
public String login() {
    return "login";
}
```

The controller does **not directly render `login.html` by itself**.

The reason `"login"` is enough is because of **Spring MVC's View Resolver**, usually combined with **Thymeleaf**.

---

## What happens?

When the browser requests:

```text
GET /login
```

Spring finds:

```java
@GetMapping("/login")
```

Then your method returns:

```java
return "login";
```

Spring interprets `"login"` as the **logical view name**.

The Thymeleaf View Resolver then converts it approximately like this:

```text
"login"
   ↓
prefix + view name + suffix
   ↓
classpath:/templates/ + login + .html
   ↓
classpath:/templates/login.html
```

So:

```java
return "login";
```

effectively points to:

```text
src/main/resources/templates/login.html
```

---

## Why don't we write `.html`?

Because the **View Resolver** is configured with the template's **prefix** and **suffix**.

For Thymeleaf, the typical configuration is conceptually:

```text
Prefix: classpath:/templates/
Suffix: .html
```

Therefore:

```java
return "login";
```

becomes:

```text
classpath:/templates/login.html
```

---

## Think of it this way

```text
Controller
   |
   | return "login"
   ↓
View Resolver
   |
   | adds prefix + suffix
   ↓
templates/login.html
   |
   ↓
Thymeleaf processes HTML
   |
   ↓
Browser receives the rendered page
```

---

## Controller returns a view name

So the controller returns a **view name**, not the physical filename.

For example:

```java
return "dashboard";
```

→

```text
templates/dashboard.html
```

```java
return "students";
```

→

```text
templates/students.html
```

```java
return "login";
```

→

```text
templates/login.html
```

---

## What is `Model model` in Spring MVC?

While working with Spring MVC controllers, you may have seen a method like this:

```java
@GetMapping("/courses")
public String showCourses(Model model) {

    model.addAttribute("courses", courseService.getAllCourses());

    return "courses";
}
```

Here, `Model` is an **interface provided by Spring MVC**, not your application's Model or Entity class.

It is used to transfer data from the **Controller to the View (HTML page)**.

Import:

```java
import org.springframework.ui.Model;
```

### Understanding `Model model`

- **Model:** Spring MVC interface.
- **model:** Reference variable automatically provided by Spring.
- **addAttribute():** Adds data to the Model so that the View can access it.
- **"courses":** Attribute name used to access the data in the HTML page.

### How does it work?

```text
Controller
   |
   | model.addAttribute("courses", data)
   ↓
Spring MVC Model
   |
   | Carries data
   ↓
Thymeleaf View
   |
   ↓
courses.html
   |
   ↓
Displays course information
```

### Example: Accessing Model data in Thymeleaf

In your `courses.html` file:

```html
<tr th:each="course : ${courses}">
    <td th:text="${course.courseName}"></td>
    <td th:text="${course.courseCode}"></td>
</tr>
```

Here, `${courses}` accesses the data that the Controller added using:

```java
model.addAttribute("courses", courseService.getAllCourses());
```

Spring MVC makes this data available to the Thymeleaf template during rendering.

---

## Difference between Spring MVC Model and Entity Model

Don't confuse Spring MVC's `Model` interface with your application's Model (Entity).

| Spring MVC Model | Entity Model |
|---|---|
| An interface provided by Spring MVC. | Usually a normal Java class. |
| Used to transfer data from Controller to View. | Represents application data and database structure. |
| Package: `org.springframework.ui.Model` | Example: `com.bsn.studentmanagement.model.Courses` |
| Uses `addAttribute()` to add data. | Uses fields, getters, setters and JPA annotations. |

Example of an Entity:

```java
@Entity
@Table(name = "courses")
public class Courses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseName;
    private String courseCode;
}
```

Here, `Courses` is a Java class representing the database entity.

**Important:** `Model` in Spring MVC is an interface, whereas `Courses` in your model package is a Java class.

You don't need to create the Spring MVC Model object manually. Spring provides it automatically when you declare it as a controller method parameter.

---

## Important interview point

Remember this flow:

```text
Logical View Name
        ↓
   View Resolver
        ↓
 Actual Template
```

For example:

```text
"login"
   ↓
View Resolver
   ↓
templates/login.html
```

For passing data to the view:

```text
Controller
    |
    | model.addAttribute()
    ↓
Spring MVC Model
    |
    ↓
Thymeleaf View
    |
    ↓
HTML Page
```

---

## Important Note

This behavior depends on the **view technology and its configuration**.

With **Thymeleaf**, the usual template location is:

```text
src/main/resources/templates/
```

So when you write:

```java
return "login";
```

Spring MVC + Thymeleaf resolves it to:

```text
src/main/resources/templates/login.html
```

The controller is therefore **returning the logical view name**, while the **View Resolver finds the actual HTML template**.

Similarly, the `Model` interface helps the controller pass dynamic data to that HTML template.
