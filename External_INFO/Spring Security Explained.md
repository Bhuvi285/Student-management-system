# Spring Security Configuration Explained

This document explains the Spring Security configuration used in the Student Management System project.

---

# `@EnableWebSecurity`

```java
@EnableWebSecurity
```

## Definition

`@EnableWebSecurity` enables Spring Security for the application.

It tells Spring Boot:

> **"Use the custom security configuration present in this class."**

Without this annotation, Spring may use its default security configuration instead of your custom rules.

---

# `PUBLIC_PATH`

```java
private static final String[] PUBLIC_PATH = {
    "/login",
    "/css/**",
    "/images/**",
    "/js/**",
    "/error"
};
```

## Definition

`PUBLIC_PATH` is an array that stores all URLs which should be accessible **without user authentication**.

These URLs are called **Public Endpoints**.

### Accessible URLs

| URL | Purpose |
|------|----------|
| `/login` | Login page |
| `/css/**` | CSS files |
| `/images/**` | Images |
| `/js/**` | JavaScript files |
| `/error` | Error page |

### Why is it required?

When a user opens the application, resources like CSS, JavaScript, and images must load before login.

If these paths were protected, the login page would appear broken because its resources couldn't be accessed.

---

# `SecurityFilterChain`

```java
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
```

## Definition

`SecurityFilterChain` is the central configuration component of Spring Security.

It defines:

- Which URLs are protected
- Which URLs are public
- Login behavior
- Logout behavior
- Authentication rules
- Authorization rules

Think of it as the **rule book** that Spring Security follows for every incoming request.

---

# `authorizeHttpRequests()`

```java
http.authorizeHttpRequests(auth -> ...)
```

## Definition

This method starts configuring **authorization rules**.

Authorization determines:

> **Who can access which URLs.**

Every request made to the application passes through these authorization rules.

---

# `requestMatchers()`

```java
.requestMatchers(PUBLIC_PATH).permitAll()
```

## Definition

`requestMatchers()` selects the URLs to which a rule should apply.

Here it matches every path present in the `PUBLIC_PATH` array.

### `permitAll()`

Allows every user to access these URLs without logging in.

Example:

```
/login
/css/style.css
/js/script.js
/images/logo.png
```

All of these are accessible even to anonymous users.

---

# `anyRequest()`

```java
.anyRequest().authenticated()
```

## Definition

This tells Spring Security:

> Every request that is **not listed** in `PUBLIC_PATH` requires authentication.

### Examples

```
/dashboard
/student/add
/student/edit
/student/delete
/admin
/profile
```

All these URLs require the user to log in first.

---

# `formLogin()`

```java
.formLogin(form -> ...)
```

## Definition

Enables **form-based authentication**.

Instead of using HTTP Basic Authentication, users log in through an HTML login page.

---

# `loginPage()`

```java
.loginPage("/login")
```

## Definition

Specifies the custom login page.

Instead of displaying Spring Security's default login page, Spring redirects users to:

```
/login
```

Your controller should return the login view for this URL.

---

# `defaultSuccessUrl()`

```java
.defaultSuccessUrl("/dashboard", true)
```

## Definition

Specifies where users should be redirected after a successful login.

### Here

```
/dashboard
```

will always open after login.

### Meaning of `true`

```java
.defaultSuccessUrl("/dashboard", true)
```

Always redirect users to `/dashboard`.

Even if they originally requested another protected page.

---

### If `false` is used

```java
.defaultSuccessUrl("/dashboard", false)
```

Spring first checks whether the user originally requested another protected page.

Example

User tries:

```
/student/edit
```

↓

Spring redirects to

```
/login
```

↓

After successful login

↓

Spring sends the user back to

```
/student/edit
```

instead of `/dashboard`.

---

# `permitAll()`

```java
.permitAll()
```

## Definition

Allows every user to access the login page.

Without this:

Spring would also secure

```
/login
```

which would create an infinite redirect loop.

Example

```
User opens /login

↓

Spring says Login Required

↓

Redirect to /login

↓

Again Login Required

↓

Infinite Loop
```

---

# `logout()`

```java
.logout(logout -> ...)
```

## Definition

Configures the logout functionality of Spring Security.

It defines what should happen after the user logs out.

---

# `logoutSuccessUrl()`

```java
.logoutSuccessUrl("/login?logout")
```

## Definition

Specifies the URL where users are redirected after a successful logout.

Here,

```
/login?logout
```

is opened.

The query parameter

```
?logout
```

can be used to display a success message like:

```
You have been logged out successfully.
```

---

# `http.build()`

```java
return http.build();
```

## Definition

Builds the configured `SecurityFilterChain` object.

Spring Security then uses this object to secure every HTTP request.

Without calling `build()`, the security configuration is incomplete.

---

# `PasswordEncoder`

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

## Definition

Creates a `PasswordEncoder` bean.

This encoder is responsible for:

- Encrypting passwords before storing them in the database.
- Comparing raw passwords with encrypted passwords during login.

---

# Why BCrypt?

BCrypt is a secure hashing algorithm because it:

- Hashes passwords instead of storing plain text.
- Generates a different hash for the same password using a random salt.
- Protects against rainbow table attacks.
- Is the recommended password encoder in Spring Security.

Example

Password entered:

```
admin123
```

Stored in database:

```
$2a$10$D7nLrM9Q4VYVt7K8bR4uEec0Wm7Kj9Q5S3p8NwVxF0r2Hc5YqZ7Lm
```

The original password cannot be retrieved from the hash.

---

# Security Flow of the Application

```text
                User Opens Application
                         │
                         ▼
               Requests a URL
                         │
                         ▼
         Is the URL in PUBLIC_PATH?
              │                 │
             Yes               No
              │                 │
              ▼                 ▼
      Access Granted      Is User Logged In?
                                │
                     ┌──────────┴──────────┐
                     │                     │
                    No                    Yes
                     │                     │
                     ▼                     ▼
            Redirect to /login      Allow Request
                     │
                     ▼
             User Enters Credentials
                     │
                     ▼
          Authentication Successful?
                     │
             ┌───────┴────────┐
             │                │
            No               Yes
             │                │
             ▼                ▼
     Show Login Again     Redirect to
                          /dashboard
```

---

# Why `throws Exception`?

```java
public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception
```

## Definition

Spring Security methods such as:

```java
http.build();
```

can throw checked exceptions.

Adding:

```java
throws Exception
```

allows these exceptions to propagate without requiring a try-catch block.

This is the recommended approach in **Spring Security 6** and **Spring Boot 3**.

---

# Summary

| Component | Purpose |
|------------|---------|
| `@EnableWebSecurity` | Enables Spring Security |
| `PUBLIC_PATH` | Stores public URLs |
| `SecurityFilterChain` | Configures application security |
| `authorizeHttpRequests()` | Starts authorization configuration |
| `requestMatchers()` | Selects URLs for specific rules |
| `permitAll()` | Allows public access |
| `anyRequest().authenticated()` | Secures all remaining URLs |
| `formLogin()` | Enables form-based authentication |
| `loginPage()` | Uses a custom login page |
| `defaultSuccessUrl()` | Redirects users after successful login |
| `logout()` | Configures logout behavior |
| `logoutSuccessUrl()` | Redirects after logout |
| `http.build()` | Builds the security filter chain |
| `PasswordEncoder` | Encrypts and verifies passwords |
| `BCryptPasswordEncoder` | Secure password hashing implementation |
| `throws Exception` | Handles checked exceptions from Spring Security |
