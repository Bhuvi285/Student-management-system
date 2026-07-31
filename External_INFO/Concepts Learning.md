# CSRF (Cross-Site Request Forgery)

## What is CSRF?

**CSRF (Cross-Site Request Forgery)** is a type of cyberattack in which a malicious website tricks an authenticated user into performing an unwanted action on another website without the user's knowledge or consent.

In simple words:

> **A hacker uses your already logged-in session to perform actions on your behalf.**

---

# Real-Life Example

Suppose you are logged into your online banking website.

```text
www.mybank.com
```

After login, your browser stores a **Session Cookie**.

Now you visit another website:

```text
www.fakewebsite.com
```

That malicious website secretly sends the following request to your bank:

```http
POST /transfer

Amount = 10000
To = Hacker
```

Since your browser automatically attaches the banking session cookie, the bank assumes the request is coming from you.

Without CSRF protection:

```text
You Login
     │
     ▼
Session Cookie Stored
     │
     ▼
Visit Fake Website
     │
     ▼
Fake Website Sends POST Request
     │
     ▼
Browser Automatically Adds Session Cookie
     │
     ▼
Bank Accepts Request ❌
```

As a result, money could be transferred without your permission.

---

# How Spring Security Prevents CSRF

Spring Security generates a unique **CSRF Token** for every authenticated session.

Example:

```text
CSRF Token

8ab34ef782cd91
```

Every state-changing request (**POST, PUT, PATCH, DELETE**) must include this token.

Example:

```http
POST /students

Name = Rahul

Token = 8ab34ef782cd91
```

Spring Security compares:

```text
Request Token

        VS

Server Stored Token
```

If both tokens match:

```text
✔ Request Accepted
```

If they do not match:

```text
❌ Request Rejected
```

---

# What Happens When the CSRF Token is Missing?

Suppose the client sends:

```http
POST /students
```

without sending the CSRF token.

Spring Security rejects the request.

Internally it throws one of the following exceptions:

```java
InvalidCsrfTokenException
```

or

```java
MissingCsrfTokenException
```

Both of these exceptions extend:

```java
CsrfException
```

The client receives:

```http
HTTP/1.1 403 Forbidden
```

---

# CSRF Exception Hierarchy

```text
Exception
    │
RuntimeException
    │
AccessDeniedException
    │
CsrfException
    │
 ┌───────────────┴────────────────┐
 │                                │
InvalidCsrfTokenException   MissingCsrfTokenException
```

---

# Why Does Spring Return HTTP 403 Forbidden?

Spring returns **403 Forbidden** because:

- The user is already authenticated.
- The request is not trusted because the CSRF token is missing or invalid.

Therefore:

```text
✔ User Identity Verified

✘ Request Authenticity Failed

→ HTTP 403 Forbidden
```

Instead of:

```text
401 Unauthorized
```

---

# Why Do REST APIs Usually Disable CSRF?

Most modern Spring Boot REST APIs use **JWT Authentication**.

Request Flow:

```text
Client
   │
Authorization: Bearer JWT
   │
Server
```

No session cookies are used.

Since CSRF attacks rely on browsers automatically sending session cookies, stateless JWT-based APIs are generally **not vulnerable to traditional CSRF attacks**.

Therefore, developers commonly disable CSRF:

```java
http.csrf(csrf -> csrf.disable());
```

---

# When Should CSRF Be Enabled?

Keep CSRF enabled when your application:

- Uses Session-based Authentication
- Uses Cookies for Authentication
- Contains HTML Forms
- Is a traditional Spring MVC Web Application

Examples:

- Banking Website
- E-Commerce Website
- Online Examination Portal
- Hospital Management Portal

---

# When Can CSRF Be Disabled?

CSRF can safely be disabled when:

- The application is a REST API
- Authentication uses JWT
- Authentication uses OAuth Bearer Tokens
- The application is completely Stateless
- No Session Cookies are used

Examples:

- Mobile Backend APIs
- Angular + Spring Boot APIs
- React + Spring Boot APIs
- Flutter Backend APIs

---

# Can `@ControllerAdvice` Handle CSRF Exceptions?

**No (in most cases).**

Reason:

CSRF validation occurs inside the **Spring Security Filter Chain**, before the request reaches the controller.

Request Flow:

```text
Client
   │
   ▼
Spring Security Filters
   │
   ├── JWT Filter
   │
   ├── Authentication Filter
   │
   ├── CSRF Validation
   │
   ├── If Token Invalid
   │        │
   │        ▼
   │   403 Forbidden Returned
   │
   ▼
DispatcherServlet
   │
   ▼
Controller
```

Since the request never reaches the controller,

`@ControllerAdvice` **cannot catch** `CsrfException`.

Instead, Spring Security handles it using:

- `AccessDeniedHandler`
- `AuthenticationEntryPoint` (for authentication-related failures)

---

# Summary

| Feature | Description |
|----------|-------------|
| Full Form | Cross-Site Request Forgery |
| Purpose | Prevent unauthorized requests using authenticated sessions |
| Security Mechanism | CSRF Token |
| Common Exceptions | `MissingCsrfTokenException`, `InvalidCsrfTokenException` |
| Parent Exception | `CsrfException` |
| HTTP Status | **403 Forbidden** |
| Used In | Session-Based Authentication |
| Disabled In | JWT Stateless REST APIs |
| Handled By | Spring Security Filter Chain |
| Handled by `@ControllerAdvice`? | ❌ No |

---

# Interview Answer

> **CSRF (Cross-Site Request Forgery)** is a web security attack in which a malicious website tricks an authenticated user into performing unintended actions on another website. Spring Security protects against this attack by generating a unique CSRF token for every session. Every state-changing request (POST, PUT, PATCH, DELETE) must include this token. If the token is missing or invalid, Spring Security throws a `CsrfException` (such as `MissingCsrfTokenException` or `InvalidCsrfTokenException`) and returns **HTTP 403 Forbidden**. In stateless JWT-based REST APIs, CSRF protection is commonly disabled because JWT authentication does not rely on browser session cookies.