# How Spring MVC Resolves `login.html` from `return "login" in Controller ?`

Yes. In your code:

```java
@GetMapping("/login")
public String login() {
    return "login";
}
```

the controller does **not directly render `login.html` by itself**.

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
:::
