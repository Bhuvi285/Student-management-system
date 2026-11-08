
# Pagination in Spring Boot – Student Management System

## 1. Introduction to Pagination

Pagination means dividing a large collection of records into smaller, manageable groups and fetching only the records required for the currently requested page.

For example, imagine your database contains 100 courses. Instead of fetching all 100 courses and displaying them together, the application fetches only 3 courses at a time.

- Page 1 → Courses 1–3
- Page 2 → Courses 4–6
- Page 3 → Courses 7–9
- And so on.

In this project, pagination is implemented using **Spring Data JPA**, with `Page`, `Pageable`, and `PageRequest`.

**Main objective:** Avoid fetching all database records at once and improve the efficiency of the course listing page.

---

## 2. Complete Pagination Architecture and Flow

Pagination involves four main application layers:

```text
Browser / Thymeleaf
       |
       | GET /course/list?page=1&size=3
       v
CourseController
       |
       | courseService.getCourses(page, size)
       v
CourseServiceImpl
       |
       | PageRequest.of(page, size, sort)
       v
CourseRepository
       |
       | findByActiveTrue(pageable)
       v
MySQL Database
       |
       | Returns requested records
       | and pagination count
       v
CourseRepository
       |
       v
CourseServiceImpl
       |
       | Entity → DTO conversion
       v
CourseController
       |
       | Model.addAttribute("courses", courses)
       v
Thymeleaf View (courses.html)
       |
       | Displays course records
       | and pagination buttons
       v
Browser
```

### Layer responsibilities

| Layer | Responsibility |
|---|---|
| Controller | Receives page and size parameters and passes data to the view |
| Service | Creates pagination request, sorting configuration, and converts Entity to DTO |
| Repository | Retrieves paginated records from the database |
| Database | Returns only the requested records |
| Thymeleaf | Displays records and dynamically generates pagination controls |

---

# 3. Repository Layer – Fetching Paginated Records

### CourseRepository.java

```java
public interface CourseRepository
        extends JpaRepository<Courses, Long> {

    boolean existsByCourseCodeIgnoreCase(String code);

    Page<Courses> findByActiveTrue(Pageable pageable);
}
```

Let's understand the important method:

```java
Page<Courses> findByActiveTrue(Pageable pageable);
```

There are three important concepts here.

### A. Page<Courses>

`Page` is an interface provided by Spring Data.

It represents a page of database records along with pagination information.

It provides:

- Records available on the current page.
- Total number of records.
- Total number of pages.
- Current page number.
- Whether the current page is the first or last page.

### B. Pageable

`Pageable` is an interface that represents pagination information.

It tells the repository:

- Which page should be fetched?
- How many records should be fetched?
- In which order should the records be sorted?

### C. findByActiveTrue()

This is a Spring Data JPA derived query method.

Spring Data understands the method name and generates a query based on the `active` property of the `Courses` entity.

Conceptually, it filters records using:

```sql
SELECT *
FROM courses
WHERE active = true;
```

When pagination and sorting are applied, the generated SQL also includes the required limit and offset.

**Important:** We do not manually write the pagination SQL query. Spring Data JPA generates the database operations based on the repository method and Pageable object.

---

# 4. Service Layer – Pagination Configuration

### CourseService.java

```java
public interface CourseService {

    CourseDTO createCourse(CourseDTO courseDTO);

    boolean existsByCourseCode(String code);

    Page<CourseDTO> getCourses(int page, int size);
}
```

The service interface declares the pagination method.

### CourseServiceImpl.java

```java
@Override
public Page<CourseDTO> getCourses(int page, int size) {

    log.info("list of course from: {}", page);

    PageRequest pageRequest = PageRequest.of(
        page,
        size,
        Sort.by(Sort.Direction.DESC, "id")
    );

    return courseRepository
        .findByActiveTrue(pageRequest)
        .map(course -> mapper.map(course, CourseDTO.class));
}
```

This is the most important part of the pagination implementation.

Let's understand it step by step.

## A. PageRequest.of(page, size, sort)

```java
PageRequest pageRequest = PageRequest.of(
    page,
    size,
    Sort.by(Sort.Direction.DESC, "id")
);
```

`PageRequest` is a concrete implementation of Spring Data's `Pageable` interface.

It creates a pagination request containing three things:

| Parameter | Meaning | Example |
|---|---|---|
| page | Which page to retrieve | 0, 1, 2... |
| size | Records per page | 3 |
| sort | Sorting order | ID descending |

### Important: Zero-Based Indexing

Spring Data uses zero-based page numbering.

| User sees | Spring receives |
|---|---:|
| Page 1 | 0 |
| Page 2 | 1 |
| Page 3 | 2 |
| Page 4 | 3 |

The first page is numbered 0 internally because Spring follows the conventional programming index system.

## B. Understanding Sort.Direction.DESC

```java
Sort.by(Sort.Direction.DESC, "id")
```

This means records should be arranged in descending order of their ID.

For example:

| ID | Course |
|---:|---|
| 109 | Java |
| 108 | Python |
| 107 | Spring Boot |
| 106 | React |
| 105 | Angular |
| 104 | MySQL |

`DESC` means descending order, whereas `ASC` means ascending order.

Because IDs are generally generated in insertion order, descending ID order usually displays the newest courses first.

## C. Repository Call

```java
courseRepository.findByActiveTrue(pageRequest);
```

Here, the service passes the pagination request to the repository.

The repository retrieves only the requested page of active courses.

## D. Entity to DTO Conversion Using Page.map()

```java
.map(course -> mapper.map(course, CourseDTO.class));
```

The repository returns:

```java
Page<Courses>
```

But the service returns:

```java
Page<CourseDTO>
```

Why?

Because DTOs control which course information is provided to the presentation layer.

Spring Data's `Page.map()` transforms the content of the page while retaining its pagination metadata.

**Important:** This `.map()` does not fetch the next page. It converts the records on the current page from Entity objects into DTO objects.

---

# 5. Controller Layer – Receiving Pagination Requests

### CourseController.java

```java
@GetMapping("/list")
public String listCourse(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "3") int size,
        Model model,
        @RequestParam(value = "message", required = false)
        String message) {

    log.info("Get /course/list - showing course list page.");

    Page<CourseDTO> courses =
            courseService.getCourses(page, size);

    model.addAttribute("courses", courses);
    model.addAttribute("message", message);

    return "courses";
}
```

This method executes whenever the browser requests `/course/list`.

## A. Understanding @RequestParam

```java
@RequestParam(defaultValue = "0") int page

@RequestParam(defaultValue = "3") int size
```

These annotations read query parameters from the URL.

For example:

```text
http://localhost:8081/course/list?page=1&size=3
```

Spring automatically assigns:

```java
page = 1;
size = 3;
```

If the URL contains no parameters:

```text
http://localhost:8081/course/list
```

Spring uses the default values:

```java
page = 0;
size = 3;
```

Therefore, the application displays the first three courses.

## B. Calling the Service

```java
Page<CourseDTO> courses =
        courseService.getCourses(page, size);
```

The controller does not write database queries or calculate SQL offsets itself.

It delegates the operation to the service layer.

The service handles pagination configuration and retrieves the data through the repository.

## C. Passing Data to Thymeleaf

```java
model.addAttribute("courses", courses);
```

Here, the complete `Page<CourseDTO>` object is passed to the Thymeleaf view.

The HTML page can access both the course records and their pagination information.

For example:

```html
${courses.content}
${courses.totalPages}
${courses.number}
${courses.first}
${courses.last}
${courses.size}
```

These properties are available because the controller passes a Page object, not just a normal List.

Finally:

```java
return "courses";
```

Spring MVC resolves this logical view name to:

```text
src/main/resources/templates/courses.html
```

---

# 6. How Pagination Works in MySQL

Suppose your database contains eight active courses, with IDs from 101 to 108.

Your application uses:

```java
PageRequest.of(
    page,
    size,
    Sort.by(Sort.Direction.DESC, "id")
);
```

The page size is 3.

Because sorting is descending, the records are considered in this order:

| Position | Course ID |
|---:|---:|
| 1 | 108 |
| 2 | 107 |
| 3 | 106 |
| 4 | 105 |
| 5 | 104 |
| 6 | 103 |
| 7 | 102 |
| 8 | 101 |

## Understanding LIMIT and OFFSET

Pagination in SQL is commonly implemented using `LIMIT` and `OFFSET`.

- `LIMIT` specifies how many records to retrieve.
- `OFFSET` specifies how many records to skip before retrieving them.

The offset is calculated using:

\[
\text{OFFSET} = \text{Page Number} \times \text{Page Size}
\]

### Example calculations

| Page | Spring page number | Page size | OFFSET |
|---|---:|---:|---:|
| Page 1 | 0 | 3 | 0 |
| Page 2 | 1 | 3 | 3 |
| Page 3 | 2 | 3 | 6 |

For example, when the browser requests:

```text
/course/list?page=1&size=3
```

Spring Data conceptually produces a database query like:

```sql
SELECT *
FROM courses
WHERE active = true
ORDER BY id DESC
LIMIT 3 OFFSET 3;
```

The database skips the first three matching records and returns the next three.

The actual SQL is generated by Hibernate and the database dialect. This query illustrates the pagination operation.

## Understanding totalPages

Spring Data also needs to know how many pages are available.

For a `Page` result, it obtains the total number of matching records, generally using a count query such as:

```sql
SELECT COUNT(*)
FROM courses
WHERE active = true;
```

Suppose:

- Total active courses = 8
- Page size = 3

Then:

\[
\text{Total Pages} =
\left\lceil \frac{8}{3} \right\rceil = 3
\]

So Spring knows there are three pages.

This is why returning `Page<Courses>` is useful: it contains both the records and pagination metadata.

---

# 7. Thymeleaf – Generating Pagination Controls

Now let's understand the frontend implementation in `courses.html`.

## A. Displaying Course Records

```html
<tr th:each="course : ${courses.content}">
    <td th:text="${course.id}">101</td>
    <td th:text="${course.courseName}">Course Name</td>
    <td th:text="${course.courseCode}">CODE</td>
    <td th:text="${course.fee}">3999</td>
    <td th:text="${course.duration}">4 Months</td>
</tr>
```

### Understanding the expressions

```html
${courses.content}
```

`content` contains only the records belonging to the current page.

```html
th:each="course : ${courses.content}"
```

This iterates through those records and generates a table row for each course.

It does not iterate through every course stored in MySQL.

## B. Previous Button

```html
<li class="page-item"
    th:classappend="${courses.first} ? 'disabled'">

    <a class="page-link"
       th:href="@{/course/list(page=${courses.number > 0 ? courses.number - 1 : 0}, size=${courses.size})}">
        Previous
    </a>
</li>
```

Two important properties are being used:

- `courses.first` tells whether the current page is the first page.
- `courses.number` gives the current zero-based page number.

For example:

- On page 0, Previous is disabled.
- On page 1, Previous leads to page 0.
- On page 2, Previous leads to page 1.

## C. Page Number Buttons

```html
<li class="page-item"
    th:each="pageNum : ${#numbers.sequence(0, courses.totalPages - 1)}"
    th:classappend="${pageNum == courses.number} ? 'active' : ''">

    <a class="page-link"
       th:href="@{/course/list(page=${pageNum}, size=${courses.size})}"
       th:text="${pageNum + 1}">
        1
    </a>
</li>
```

This is where Thymeleaf dynamically generates the page numbers.

### 1. #numbers.sequence()

```html
${#numbers.sequence(0, courses.totalPages - 1)}
```

Creates a sequence of page numbers.

If `totalPages = 3`, the sequence is:

```text
0, 1, 2
```

Thymeleaf loops through these values and creates three page buttons.

### 2. pageNum == courses.number

This checks whether the page number being rendered is the currently selected page.

When true, it adds Bootstrap's `active` class to highlight the current page.

### 3. pageNum + 1

Spring uses zero-based indexing, but users should see normal page numbering.

| Spring page number | Displayed number |
|---:|---:|
| 0 | 1 |
| 1 | 2 |
| 2 | 3 |

### 4. courses.size

This retains the selected page size when the user clicks another page.

## D. Next Button

```html
<li class="page-item"
    th:classappend="${courses.last} ? 'disabled'">

    <a class="page-link"
       th:href="@{/course/list(page=${courses.last ? courses.number : courses.number + 1}, size=${courses.size})}">
        Next
    </a>
</li>
```

Here, `courses.last` determines whether the current page is the final page.

- If it is the last page, Next is disabled.
- Otherwise, it navigates to the next page.

---

# 8. Complete Next and Previous Button Flow

Suppose you are currently on Page 1:

```text
http://localhost:8081/course/list?page=0&size=3
```

You click Next.

### Step 1 – Browser

The Next link takes you to:

```text
http://localhost:8081/course/list?page=1&size=3
```

### Step 2 – Controller

Spring reads:

```java
page = 1;
size = 3;
```

### Step 3 – Service

It creates:

```java
PageRequest.of(
    1,
    3,
    Sort.by(Sort.Direction.DESC, "id")
);
```

### Step 4 – Repository

The repository receives the PageRequest and retrieves the second page of active courses.

### Step 5 – Database

The database skips three records and returns the next three.

### Step 6 – Thymeleaf

The controller passes the new `Page<CourseDTO>` to `courses.html`.

Thymeleaf displays the new three records and highlights Page 2.

This entire process happens again whenever the user clicks another pagination link.

**Important:** The browser makes another GET request. Pagination is not simply hiding and showing existing HTML rows; the application requests another subset of database records.

---

# 9. Understanding the Four Pagination Commits

The pagination feature was implemented through four commits on **8 November 2026**.

| Commit time (IST) | Commit | What changed |
|---|---|---|
| 5:26:21 PM | [b223046](https://github.com/Bhuvi285/Student-management-system/commit/b223046d52b53ee11612115b38d6a53958d76c70) | Added pagination to CourseController |
| 5:26:36 PM | [ff24a1a](https://github.com/Bhuvi285/Student-management-system/commit/ff24a1ab38b595b2fbb68be303222498d01952e7) | Added pagination in Service and Service interface |
| 5:26:50 PM | [cbc266f](https://github.com/Bhuvi285/Student-management-system/commit/cbc266fd8eef6682db1cd244c89b3358b983602c) | Added pagination query in Repository |
| 5:27:01 PM | [a8e4f92](https://github.com/Bhuvi285/Student-management-system/commit/a8e4f92724a2402df33fdbae469bd92921eb4734) | Updated courses.html with dynamic course rows and pagination controls |

These commits fit together in this order:

**Repository → Service → Controller → Thymeleaf View**

The commits were created chronologically starting with the controller, followed by the service, repository, and view.

---

# 10. Improvements Needed in the Current Implementation

After inspecting the repository, I noticed a few things worth fixing before presenting this feature as production-ready.

## Issue 1: Static Mockup Rows

In your current `courses.html`, after the dynamic row:

```html
<tr th:each="course : ${courses.content}">
```

you still have two hardcoded rows:

- Web Development Bootcamp
- Data Structures & Algorithms

These rows are outside the `th:each` loop.

That means they appear on every page whenever the dynamic table is displayed. They are not part of the actual database pagination.

**Solution:** Remove those two static rows. Your table should contain only the dynamic `th:each` row.

## Issue 2: Page and Size Validation

Your controller accepts values directly from the URL.

For example:

```text
/course/list?page=-1&size=3
/course/list?page=0&size=0
```

Invalid values can cause `PageRequest.of()` to throw an `IllegalArgumentException`.

A simple improvement is to validate the values before passing them to the service:

```java
page = Math.max(page, 0);
size = Math.min(Math.max(size, 1), 50);
```

This ensures:

- Page is non-negative.
- Page size is between 1 and 50.

## Issue 3: Success Message Handling

Your current controller still has:

```java
@RequestParam(value = "message", required = false)
String message
```

And:

```java
model.addAttribute("message", message);
```

But your POST method uses:

```java
redirectAttributes.addFlashAttribute(
    "message",
    "Course is created successfully!"
);
```

Since flash attributes are already made available to the redirected request, remove the `@RequestParam` and `model.addAttribute("message", message)` lines.

This avoids a possible collision with a null request parameter.

## Issue 4: Showing Too Many Page Numbers

Currently, this expression:

```html
${#numbers.sequence(0, courses.totalPages - 1)}
```

generates every page number.

For a small number of courses, that is fine.

But with 1,000 courses and a page size of 3, you would have 334 page buttons.

Later, you can implement a sliding window showing approximately five page numbers around the current page.

---

# 11. Recruiter Interview Explanation

Here is a practical explanation that you can use during a technical interview.

### Interview Answer

"In my Student Management System, I implemented server-side pagination using Spring Data JPA.

The purpose of pagination is to avoid fetching and displaying all course records at once. Instead, the application retrieves a limited number of records for each page.

I implemented it across the Controller, Service, Repository, and Thymeleaf View layers.

First, the CourseController accepts the page number and page size through request parameters. By default, it loads page 0 with 3 records.

Then, the controller calls the CourseService, which creates a PageRequest using the requested page number, page size, and sorting direction.

I use descending order on the course ID so that the newest courses appear first.

The PageRequest is passed to the CourseRepository, where I use a Spring Data JPA derived query called findByActiveTrue. It retrieves only active courses according to the pagination request.

The repository returns a Page of Course entities. In the service layer, I use the Page.map() method along with ModelMapper to convert those entities into CourseDTO objects while preserving pagination metadata.

Finally, the controller adds the Page of DTOs to the Model, and Thymeleaf uses its content and pagination properties to dynamically render the course table, page numbers, Previous button, and Next button.

When a user clicks another page, the browser sends a new GET request with the updated page parameter, and the entire process repeats for that page.

This makes the course listing more efficient and keeps the presentation layer separate from the database entity structure."

---

# 12. Recruiter Follow-Up Questions

These are the questions you should prepare for after explaining your implementation.

### Q1. What is the difference between Page and Pageable?

**Pageable:** Represents the pagination request, including page number, page size, and sorting.

**Page:** Represents the result, including the records and pagination metadata.

### Q2. Why are you using PageRequest?

`PageRequest` is a concrete implementation of `Pageable`. I use it to create the pagination request and specify sorting.

### Q3. Why is Spring's page numbering zero-based?

Spring Data uses zero-based indexing.

Therefore, the first page is 0, the second is 1, and so on. The UI converts these values into user-friendly numbering using `pageNum + 1`.

### Q4. What is the difference between LIMIT and OFFSET?

`LIMIT` decides how many records to retrieve, while `OFFSET` decides how many records to skip before retrieving them.

For example:

```sql
LIMIT 3 OFFSET 6;
```

Skips six records and retrieves the next three.

### Q5. Why do you use Page<Courses> instead of List<Courses>?

A List contains records but does not automatically provide total pages, total elements, or first/last-page information.

A Page provides both the current records and pagination metadata.

### Q6. Why do you use Page.map()?

To convert Entity objects into DTO objects without losing the pagination metadata.

It transforms the page content, not the pagination itself.

### Q7. What is the benefit of server-side pagination?

It reduces the number of records transferred to the application for each request and avoids loading the entire result set into memory just to display one page.

### Q8. What happens when the user requests a page that doesn't exist?

Spring Data generally returns an empty page when the requested page is beyond the available pages.

A negative page number is invalid and must be handled separately.

---

# 13. Quick Revision – Important Concepts

| Concept | Meaning |
|---|---|
| Page | Contains current page records and pagination metadata |
| Pageable | Represents pagination request information |
| PageRequest | Concrete implementation of Pageable |
| PageRequest.of() | Creates pagination request |
| Sort.Direction.DESC | Sorts records in descending order |
| findByActiveTrue() | Retrieves active courses |
| Page.map() | Converts Entity content into DTO content |
| LIMIT | Number of records to retrieve |
| OFFSET | Number of records to skip |
| courses.content | Records on the current page |
| courses.totalPages | Total number of pages |
| courses.number | Current zero-based page number |
| courses.first | Checks whether it is the first page |
| courses.last | Checks whether it is the last page |
| courses.size | Number of records per page |

### Important Formula

\[
\text{OFFSET} = \text{Page Number} \times \text{Page Size}
\]

\[
\text{Total Pages} =
\left\lceil
\frac{\text{Total Records}}{\text{Page Size}}
\right\rceil
\]

---

# 14. Final Interview Preparation

Do not just memorize the interview explanation.

Make sure you can explain these five concepts confidently:

1. `Page` vs `Pageable`.
2. `PageRequest.of(page, size, sort)`.
3. `LIMIT` and `OFFSET`.
4. `Page.map()` with ModelMapper.
5. How Thymeleaf uses pagination metadata to generate navigation buttons.

The strongest interview approach is to explain the flow using one actual example:

**50 courses, 5 records per page, user clicks Page 3, Spring calculates OFFSET 10, and MySQL returns the next five active courses.**

That demonstrates that you understand the implementation rather than merely knowing the annotations.

---

**Repository:** [Student Management System – GitHub](https://github.com/Bhuvi285/Student-management-system)

**Implementation:** Spring Boot + Spring MVC + Spring Data JPA + MySQL + Thymeleaf + ModelMapper.
