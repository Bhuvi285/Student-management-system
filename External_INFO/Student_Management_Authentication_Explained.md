# Student Management System — Authentication Flow Explained

This document explains the authentication flow in the **Student Management System** using:

- `AuthController.java`
- `UserServiceImpl.java`
- `UsersRepository.java`
- `Users.java`
- Spring Security
- `UserDetails`
- `PasswordEncoder`

---

# 1. Big Picture

Your authentication works roughly like this:

```text
User enters username + password
              ↓
       /login request
              ↓
      Spring Security
              ↓
     UserServiceImpl
              ↓
      UsersRepository
              ↓
        users table
              ↓
   UserDetails returned
              ↓
 Spring Security checks password
              ↓
       Authentication
       SUCCESS / FAILURE
```

The most important point is:

> You don't manually check the password in `AuthController` or `UserServiceImpl`. Spring Security does the actual authentication.

Your code mainly tells Spring Security **where to find the user and their password**.

---

# 2. `AuthController` — Displays the Login Page

```java
package com.bsn.studentmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
```

When the user visits:

```text
http://localhost:8080/login
```

Spring MVC finds:

```java
@GetMapping("/login")
```

and executes:

```java
return "login";
```

This tells Spring MVC:

> "Show the login view."

For example, if you're using Thymeleaf, it may be:

```text
src/main/resources/templates/login.html
```

## Important

`AuthController` **does NOT authenticate the user**.

It only provides the login page.

```text
GET /login
     ↓
AuthController
     ↓
login.html
```

---

# 3. User Enters Credentials

Suppose your login page contains:

```text
Username: bhuvi
Password: 123456
        ↓
       Login
```

The browser sends the credentials to the login endpoint.

Conceptually:

```text
POST /login

username=bhuvi
password=123456
```

Now **Spring Security takes control**.

Your `AuthController` does not process this `POST /login` authentication request.

---

# 4. Spring Security Needs to Find the User

Spring Security receives something like:

```text
Username = bhuvi
Password = 123456
```

But it needs to know:

> "Where should I find user `bhuvi`?"

This is where your:

```java
UserServiceImpl
```

comes in.

---

# 5. `UserServiceImpl` — Connects Spring Security to Your Database

Your class implements:

```java
UserDetailsService
```

and looks like this:

```java
@Service
public class UserServiceImpl implements UserDetailsService
```

This is very important.

`UserDetailsService` is a Spring Security interface that basically says:

> "Give me a username, and I'll give you the user's security information."

That's why you implement:

```java
loadUserByUsername()
```

Your class acts as the bridge between **Spring Security** and your **database access layer**.

---

# 6. `loadUserByUsername()` Gets Called

Your method is:

```java
@Override
public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {
```

Suppose the user entered:

```text
bhuvi
```

Spring Security effectively asks your service:

```text
loadUserByUsername("bhuvi")
```

Then your code executes:

```java
Users users = userRepository.findByUsername(username)
        .orElseThrow(() ->
            new UsernameNotFoundException("Invalid Username"));
```

---

# 7. `UsersRepository` Searches the Database

Your repository contains:

```java
Optional<Users> findByUsername(String username);
```

Because your repository extends:

```java
JpaRepository<Users, Long>
```

Spring Data JPA automatically creates the implementation for:

```java
findByUsername()
```

You don't need to write SQL manually.

Conceptually, Spring Data JPA performs something similar to:

```sql
SELECT *
FROM users
WHERE username = 'bhuvi';
```

Suppose your database contains:

| id | username | password | active |
|---:|---|---|---|
| 1 | bhuvi | encoded-password | true |

Then:

```java
findByUsername("bhuvi")
```

returns that `Users` object.

---

# 8. What Happens if the Username Doesn't Exist?

This part:

```java
.orElseThrow(() ->
    new UsernameNotFoundException("Invalid Username"));
```

means:

```text
User found?
   │
   ├── YES → continue
   │
   └── NO → throw UsernameNotFoundException
```

So if the user enters:

```text
Username: abc
```

and `abc` doesn't exist:

```text
Database
   ↓
No user found
   ↓
UsernameNotFoundException
   ↓
Spring Security authentication fails
```

The exception tells Spring Security that the username could not be loaded.

---

# 9. Returning `UserDetails`

If the user exists, you have:

```java
Users users
```

containing:

```text
username
password
active
```

Then you create:

```java
return User.withUsername(username)
        .password(users.getPassword())
        .disabled(!users.isActive())
        .build();
```

This is extremely important.

You are converting your database `Users` entity into a Spring Security `UserDetails` object.

Think of it like:

```text
Database User
     ↓
Users entity
     ↓
UserDetails
     ↓
Spring Security
```

---

# 10. Why Do We Need `UserDetails`?

Your database entity is:

```java
Users
```

Spring Security expects a standard security representation:

```java
UserDetails
```

So you're basically saying:

> "Spring Security, here is the user you asked for, along with the password and account status."

`UserDetails` contains the information Spring Security needs to evaluate the user, such as the username, password, account state, and authorities/roles when those are configured.

In your current code, you are specifically providing the username, password, and enabled/disabled state.

---

# 11. Understanding `User.withUsername()` and Account Status

## `User.withUsername(username)`

```java
User.withUsername(username)
```

You are creating a Spring Security user representation using the username loaded from the database.

## `.password(users.getPassword())`

```java
.password(users.getPassword())
```

gives Spring Security the password stored in your database.

It does **not** by itself compare the password with the value entered by the user.

## `.disabled(!users.isActive())`

```java
.disabled(!users.isActive())
```

checks whether the account is active.

For example:

```java
active = true
```

then:

```java
disabled(!true)
disabled(false)
```

So the account is **not disabled**.

If:

```java
active = false
```

then:

```java
disabled(!false)
disabled(true)
```

So Spring Security considers the account disabled.

---

# 12. The Password Is NOT Checked in `UserServiceImpl`

This is one of the most important things to understand.

Your code:

```java
.password(users.getPassword())
```

does **not** mean:

> "Check whether the entered password is correct."

It means:

> "Here is the stored password associated with this username."

Spring Security's authentication mechanism then compares the submitted password with the stored password, normally through the configured `PasswordEncoder`.

For example:

```text
User enters:

username = bhuvi
password = 123456
```

Database:

```text
username = bhuvi
password = $2a$10$........
```

Spring Security:

```text
Submitted password
        ↓
PasswordEncoder
        ↓
Compare with stored encoded password
        ↓
MATCH?
```

If the password matches:

```text
Authentication SUCCESS
```

If it does not match:

```text
Authentication FAILURE
```

---

# 13. Where Does `PasswordEncoder` Come In?

You haven't included your complete Security configuration here, but a typical configuration contains something like:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

During authentication:

```text
Entered password
       ↓
PasswordEncoder
       ↓
Compare with database password
```

For example:

```text
Entered:
123456

Database:
$2a$10$abc....
```

The database normally stores the **encoded/hash representation**, not the plain-text password.

With BCrypt, Spring Security uses the stored hash to verify whether the submitted password is correct.

---

# 14. Complete Authentication Flow of Your Project

Now put all the classes together.

## Step 1 — User Opens the Login Page

```text
GET /login
     ↓
AuthController
     ↓
return "login"
     ↓
login.html
```

The controller's job here is simply to return the login view.

---

## Step 2 — User Enters Credentials

```text
Username: bhuvi
Password: 123456
```

and submits the form:

```text
POST /login
```

---

## Step 3 — Spring Security Receives the Login Request

```text
POST /login
      ↓
Spring Security
```

Spring Security extracts the submitted credentials, conceptually:

```text
username = bhuvi
password = 123456
```

---

## Step 4 — Spring Security Calls Your User Service

Spring Security uses your `UserDetailsService` implementation:

```java
loadUserByUsername("bhuvi")
```

Your service then calls:

```java
userRepository.findByUsername("bhuvi")
```

---

## Step 5 — Repository Queries the Database

Conceptually:

```sql
SELECT *
FROM users
WHERE username = 'bhuvi';
```

The result becomes a:

```text
Users object
```

---

## Step 6 — `UserServiceImpl` Converts It to `UserDetails`

```java
return User.withUsername(username)
        .password(users.getPassword())
        .disabled(!users.isActive())
        .build();
```

Result:

```text
UserDetails
```

---

## Step 7 — Spring Security Checks the Credentials

Conceptually:

```text
Entered password
       ↓
PasswordEncoder
       ↓
Stored password hash
       ↓
     Compare
```

It also checks the account state, such as whether the account is disabled.

---

## Step 8 — Authentication Result

If the credentials are valid and the account is allowed to authenticate:

```text
Authentication SUCCESS
        ↓
SecurityContext
        ↓
User is authenticated
        ↓
Access protected endpoints
```

If the credentials are invalid or the account cannot authenticate:

```text
Authentication FAILURE
        ↓
Login failure
```

---

# 15. Role of Each Class / Component

| Class / Component | Main Responsibility |
|---|---|
| `AuthController` | Displays the login page |
| `UserServiceImpl` | Finds the user and provides security information to Spring Security |
| `UsersRepository` | Retrieves the user from the database |
| `Users` | Represents the `users` database table as a JPA entity |
| `UserDetails` | Spring Security's standard representation of the user |
| `PasswordEncoder` | Verifies the submitted password against the stored encoded password |
| Spring Security | Coordinates authentication and decides whether the request is authenticated |

---

# 16. One Very Important Architectural Distinction

Don't think of authentication as simply:

```text
AuthController
     ↓
UserService
     ↓
Repository
     ↓
Authentication
```

Your actual flow is closer to:

```text
                 ┌── AuthController
                 │      ↓
Browser ── /login ──→ Login Page
                 │
                 ↓
          Spring Security
                 │
                 ↓
       UserDetailsService
                 │
                 ↓
        UserServiceImpl
                 │
                 ↓
       UsersRepository
                 │
                 ↓
            Database
                 │
                 ↓
          UserDetails
                 │
                 ↓
       Spring Security
                 │
          ┌──────┴──────┐
          ↓             ↓
       SUCCESS        FAILURE
```

There is an important difference between the **login page request** and the **actual authentication request**:

```text
GET /login
   ↓
AuthController
   ↓
Display login.html
```

but:

```text
POST /login
   ↓
Spring Security
   ↓
UserDetailsService
   ↓
UserServiceImpl
   ↓
UsersRepository
   ↓
Database
   ↓
UserDetails
   ↓
PasswordEncoder + account checks
   ↓
SUCCESS / FAILURE
```

---

# 17. Your `Users` Entity and Its Role

Your entity is:

```java
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean active;

    // getters and setters
}
```

It represents a row in the `users` table.

For example:

| id | username | password | active |
|---:|---|---|---|
| 1 | bhuvi | encoded-password | true |

Spring Data JPA maps this database row into:

```java
Users users
```

Then `UserServiceImpl` reads that object and builds the Spring Security `UserDetails` object from it.

---

# 18. Understanding the Repository Method

Your repository is:

```java
public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByUsername(String username);

    Optional<Users> findByUsername(String username);
}
```

## `existsByUsername()`

```java
boolean existsByUsername(String username);
```

Checks whether a username already exists.

For example, it can be useful during registration:

```text
User enters username
        ↓
existsByUsername()
        ↓
Already exists?
```

## `findByUsername()`

```java
Optional<Users> findByUsername(String username);
```

Searches for the actual user record by username.

It returns `Optional<Users>` because the username may or may not exist.

---

# 19. Easy Mental Model to Remember

Think about the components as different people doing different jobs.

### Controller

> "Show the login page."

### Spring Security

> "Handle the login process and decide whether authentication succeeds."

### `UserServiceImpl`

> "Tell me the username, and I will find the user's security details."

### `UsersRepository`

> "I'll go to the database and find that user."

### `Users` Entity

> "I represent the user record stored in the database."

### `UserDetails`

> "I am the standard user information that Spring Security understands."

### `PasswordEncoder`

> "I'll verify whether the submitted password matches the stored encoded password."

---

# 20. Final Authentication Flow to Memorize

For interviews and project explanation, remember this sequence:

```text
1. User opens /login
          ↓
2. AuthController returns login page
          ↓
3. User submits username + password to /login
          ↓
4. Spring Security intercepts the login request
          ↓
5. Spring Security calls loadUserByUsername(username)
          ↓
6. UserServiceImpl calls UsersRepository
          ↓
7. Repository finds the user in the users table
          ↓
8. UserServiceImpl creates UserDetails
          ↓
9. Spring Security checks stored password using PasswordEncoder
          ↓
10. Spring Security checks account state
          ↓
11. SUCCESS → authenticated user / SecurityContext
       or
    FAILURE → login rejected
```

---

# 21. One-Line Summary

> **`AuthController` displays the login page, Spring Security handles authentication, `UserServiceImpl` loads the user, `UsersRepository` fetches the user from the database, `UserDetails` gives Spring Security the required user information, and `PasswordEncoder` verifies the password.**
