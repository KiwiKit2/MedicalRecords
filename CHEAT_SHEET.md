# MedicalRecords Project – Detailed Cheat Sheet

_A comprehensive guide covering every layer of the application, key code sections, explanations, and technologies. Use this to answer any questions during your project defense._

---

## Table of Contents
1. Project Overview
2. Technology Stack
3. Architecture & Layers
   - Persistence (Entities & Repositories)
   - Service Layer (Business Logic)
   - Web Layer (Controllers & Views)
   - Security Configuration
4. Data Model & Relationships
5. Key Code Walkthroughs
   - Entity Definitions
   - Repository Methods
   - DTOs & Mapping
   - Controller Endpoints
   - Thymeleaf Fragments & Layout
   - Validation & Exception Handling
6. Testing Strategy
7. Build & Deployment
8. Configuration Files
9. Future Improvements & Best Practices

---

## 1. Project Overview
The MedicalRecords application allows administrators, doctors, and patients to manage and view patient medical records, including:
- CRUD operations on Doctors, Patients, Diagnoses, Visits, and Sick Leaves
- Role-based access: Patients see only their records; Doctors see/edit their own patients; Admins have full access
- Reporting: lists and aggregated statistics (e.g., most frequent diagnoses, visits per doctor, sick leave stats)
- Responsive UI with a shared Bootstrap navbar and active-link highlighting

---

## 2. Technology Stack
- **Java 17**
- **Spring Boot 3.5.0**
- **Spring MVC & Thymeleaf** for server-side rendering
- **Spring Data JPA** (Hibernate) for persistence
- **Spring Security** for authentication & authorization
- **Jakarta Validation** (`jakarta.validation`) for bean validation
- **Lombok** to reduce boilerplate
- **Bootstrap 5** for styling and responsive layout
- **Maven** for build & dependency management
- **H2 (in-memory)** database for development & tests
- **JUnit 5 & Spring Test** for unit and integration tests

---

## 3. Architecture & Layers

### 3.1 Persistence Layer (Entities & Repositories)
- **Entities** annotated with `@Entity`, relationships via `@ManyToOne`, `@OneToMany`
- **Repositories** extend `JpaRepository<T,ID>` for CRUD & custom queries

Example: `Visit` entity & repository
```java
@Entity
@Data @Builder
public class Visit {
  @Id @GeneratedValue(strategy=IDENTITY)
  private Long id;
  @NotNull
  @ManyToOne @JoinColumn(name="patient_id")
  private Patient patient;
  @NotNull
  @ManyToOne @JoinColumn(name="doctor_id")
  private Doctor doctor;
  private LocalDate visitDate;
  @ManyToOne @JoinColumn(name="diagnosis_id")
  private Diagnosis diagnosis;
  private String treatment;
}

public interface VisitRepository extends JpaRepository<Visit,Long> {
  List<Visit> findByPatientId(Long patientId);
  List<Visit> findByVisitDateBetween(LocalDate start, LocalDate end);
}
```

### 3.2 Service Layer (Business Logic)
- Contains reporting methods (e.g., counts, groupings)
- Interfaces to repositories and aggregates data

Example: counting visits per doctor
```java
@Service
public class ReportService {
  @Autowired
  private VisitRepository visitRepo;

  public Map<Doctor,Long> countVisitsPerDoctor(LocalDate start, LocalDate end) {
    return visitRepo.findByVisitDateBetween(start,end)
      .stream()
      .collect(groupingBy(Visit::getDoctor, counting()));
  }
}
```

### 3.3 Web Layer (Controllers & Views)
- **Controller classes** annotated `@Controller` handle UI routes
- **Method-level security** with `@PreAuthorize`
- **Model attributes** populate Thymeleaf templates

Example: `VisitUIController`
```java
@Controller @RequestMapping("/visits")
public class VisitUIController {
  @GetMapping
  public String listVisits(Model m, Authentication auth) {
    if (hasRole(auth,"PATIENT")) {
      Patient p = patientRepo.findByEgn(auth.getName()).get(0);
      m.addAttribute("visits", visitRepo.findByPatientId(p.getId()));
    } else {
      m.addAttribute("visits", visitRepo.findAll());
    }
    return "visits";
  }

  @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
  @PostMapping
  public String addVisit(@Valid @ModelAttribute Visit visit,
                         BindingResult br,
                         Authentication auth) {
    // handle binding errors & save
  }
}
```

### 3.4 Security Configuration
- No custom `WebSecurityConfigurerAdapter`; rely on auto-configured in-memory users for dev
- **Roles**: `ADMIN`, `DOCTOR`, `PATIENT`
- **Thymeleaf SEC Extras** tag namespace for conditional rendering

Security snippet in view:
```html
<li sec:authorize="hasRole('ADMIN')">
  <a th:href="@{/doctors}">Manage Doctors</a>
</li>
```

---

## 4. Data Model & Relationships

- **Doctor** 1..* → **Visit**
- **Patient** 1..* → **Visit**
- **Diagnosis** 1..* → **Visit** (optional)
- **Patient** 1 → * **SickLeave** with computed `endDate` based on `startDate + duration`

Entity relationships leverage `@ManyToOne` and join columns to enforce referential integrity.

---

## 5. Key Code Walkthroughs

### 5.1 Entity Definitions
- `@NotNull`, `@Size`, and other validation annotations
- Computed fields in `SickLeave.setDurationInDays()` and `setStartDate()`

### 5.2 Repository Methods
- Custom finder methods: `findByEgn()`, `findByPatientId()`, `findByVisitDateBetween()`

### 5.3 DTOs & Mapping
- Created standalone DTOs (e.g., `VisitDTO`, `DoctorDTO`)
- Mapping can be implemented with MapStruct or ModelMapper when refactoring

### 5.4 Controller Endpoints
- CRUD routes: `/`, `/dashboard`, `/visits`, `/visits/edit/{id}`, etc.
- Fragment insertion: `th:replace="fragments/header :: header"`

### 5.5 Thymeleaf Fragments & Layout
- Centralized `fragments/header.html` for navbar
- `@ControllerAdvice` injecting `currentUri` for active-link class toggling

### 5.6 Validation & Exception Handling
- Spring Boot handles `@Valid` and `BindingResult`
- Raise `IllegalArgumentException` for 400 and `ResponseStatusException` for 403

---

## 6. Testing Strategy

- **Unit tests** for service methods (`ReportServiceTest`)
- **Integration tests** for controllers (`DoctorUIControllerTest`, etc.) using `MockMvc`
- All tests in `src/test/java`, run with `mvn test` and generate Surefire reports in `target/surefire-reports`

---

## 7. Build & Deployment

```bash
# Clean, compile, and run tests
git clean -fdx && mvnw clean test

# Run locally
git pull && mvnw spring-boot:run
# Access at http://localhost:8080
```

For production, package a JAR:
```bash
mvnw clean package
target/medicalrecords-0.0.1-SNAPSHOT.jar
java -jar target/medicalrecords-0.0.1-SNAPSHOT.jar
```

---

## 8. Configuration Files

- **application.properties**: datasource, JPA, H2 console
- **pom.xml**: dependencies (Spring Boot Starter Web, Data JPA, Security, Thymeleaf Extras, Lombok, H2)

Key excerpt from `pom.xml`:
```xml
<dependencies>
  <dependency>spring-boot-starter-web</dependency>
  <dependency>spring-boot-starter-thymeleaf</dependency>
  <dependency>spring-boot-starter-data-jpa</dependency>
  <dependency>spring-boot-starter-security</dependency>
  <dependency>org.thymeleaf.extras:thymeleaf-extras-springsecurity6</dependency>
  <dependency>com.h2database:h2</dependency>
  <dependency>org.projectlombok:lombok</dependency>
</dependencies>
```

---

## 9. Future Improvements & Best Practices
- Swap H2 for PostgreSQL/MySQL in production
- Add DTO mapping layer (MapStruct)
- Write comprehensive API tests (Postman/Newman)
- Extract service interfaces & implement caching for heavy reports
- Frontend: React/Vue SPA calling REST endpoints

---

_Keep this sheet beside you to confidently answer any deep-dive questions on design, code, or deployment._