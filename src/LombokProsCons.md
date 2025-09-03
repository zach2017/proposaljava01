

# Lombok PRos and Cons 

* **Use Lombok builders primarily for DTOs** (request/response) and **service-layer models**.
* **Be cautious using builders for JPA entities** (Hibernate needs a no-args ctor; equals/hashCode pitfalls; partial object construction can violate constraints).
* **H2/Derby**: great for local/dev; not production workhorses.
* **PostgreSQL**: best default for prod (+ JSONB when you want document-ish fields).
* **MongoDB**: good when your core domain is inherently document-shaped and schema-flexible.

---

# 1) Lombok Builders: Pros & Cons (in Spring apps)

### Pros

* **Readable construction** for complex objects: `UserDto.builder().email(...).name(...).build()`.
* **Immutability** with `@Value` + `@Builder` (for DTOs/configs).
* **Safer optional fields**: no giant telescoping constructors.
* **toBuilder()** enables safe, partial updates: `user.toBuilder().displayName("…").build()`.

### Cons / Gotchas (especially with JPA)

* **JPA Entities need a no-args constructor** (at least `protected`), which **@Builder does not provide**. You usually end up with both `@NoArgsConstructor` and `@AllArgsConstructor` beside a builder, which can be confusing.
* **equals/hashCode**: Lombok’s defaults may include mutable IDs. For entities, use **identifier-based** equality carefully (or rely on database ID only after persistence).
* **Partial builds can violate invariants** (e.g., missing required fields), so combine with **Jakarta Validation**.
* **Lazy proxies** and builder copies can surprise you if you accidentally dereference lazily loaded associations in `toString()` or `equals()`.

### Good rule of thumb

* **DTOs**: `@Builder` ✅
* **Service-layer domain models (non-JPA)**: `@Builder` ✅
* **JPA Entities**: use sparingly; if you do, keep constructor + builder consistent and validate aggressively. Many teams prefer a **factory/service method** for entities instead of builders.

---

# 2) Datastore Options & When Builders Fit

## A) H2 (in-memory or file) – Dev/Test

**Pros:** zero setup, fast, supports most SQL, easy for integration tests.
**Cons:** not prod-grade, SQL dialect quirks vs Postgres, different behavior around constraints/JSON.
**Lombok fit:** Fine; you’ll usually be building **DTOs** and **test fixtures**.

**Gradle deps**

```gradle
dependencies {
  implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
  runtimeOnly 'com.h2database:h2'
  compileOnly 'org.projectlombok:lombok'
  annotationProcessor 'org.projectlombok:lombok'
  implementation 'org.springframework.boot:spring-boot-starter-validation'
}
```

**application.yml (dev)**

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:appdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
```

## B) Derby – Legacy/embedded

**Pros:** Embedded, file-based; used in some legacy stacks.
**Cons:** Less common in modern Spring apps; feature-limited vs Postgres; fewer community resources.
**Lombok fit:** Same as H2; good for simple embedded scenarios, but not recommended for new prod systems.

**Gradle runtimeOnly**

```gradle
runtimeOnly 'org.apache.derby:derby'
```

## C) PostgreSQL – Recommended for Prod

**Pros:** rock-solid, rich SQL, transactions, indexing, **JSONB**, strong ecosystem.
**Cons:** Needs infra; a bit more setup locally (Docker).
**Lombok fit:** Use builders for **DTOs**; be deliberate if you use builders for **entities**.

**Gradle**

```gradle
runtimeOnly 'org.postgresql:postgresql'
```

**application.yml (prod-ish)**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/appdb
    username: app
    password: secret
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

## D) “JSON database” Options

### Option 1: MongoDB (Document DB)

**Pros:** schema-flexible, nested docs, rapid iteration; easy mapping with Spring Data Mongo.
**Cons:** weaker cross-document transactions; you enforce schema at app level; joins are manual/aggregations.
**Use when:** Your domain is naturally document-shaped and evolving.

**Gradle**

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
```

### Option 2: PostgreSQL JSONB (Hybrid)

**Pros:** keep relational strengths **and** store semi-structured JSON; index JSONB fields; one DB to run.
**Cons:** you must choose carefully what’s relational vs JSON; discipline around validation/migrations.
**Use when:** Mostly relational but some fields are flexible/unstructured.

**Extra lib for easy JSON mapping (optional):**

```gradle
implementation 'com.vladmihalcea:hibernate-types-60:2.21.1' // version may vary
```

---

# 3) Example: DTO + Validation + Lombok Builder (great for React → API)

```java
// build.gradle already includes spring-boot-starter-validation and lombok

import jakarta.validation.constraints.*;
import lombok.*;

@Value
@Builder(toBuilder = true)
public class CreateUserRequest {
    @NotBlank @Email
    String email;

    @NotBlank @Size(min = 2, max = 50)
    String displayName;

    @Pattern(regexp = "USER|ADMIN", message = "role must be USER or ADMIN")
    String role;

    // Optional nested object
    @Valid
    Preferences preferences;

    @Value
    @Builder
    public static class Preferences {
        @NotNull
        Boolean marketingOptIn;

        @Size(max = 3)
        java.util.List<@NotBlank String> tags;
    }
}
```

**Controller (validation + clean 400s for React):**

```java
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest req) {
        return userService.createUser(req);
    }
}
```

**Global error handling so React gets structured messages:**

```java
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .collect(Collectors.toList());

        Map<String,Object> body = new HashMap<>();
        body.put("title", "Validation Failed");
        body.put("status", 400);
        body.put("errors", errors);

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
```

---

# 4) Example: JPA Entity (Postgres) with Careful Lombok

```java
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter // or @Data, but be careful with equals/hashCode on entities
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // used by builder
@Builder(toBuilder = true) // optional; consider factories instead
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String displayName;

    @Pattern(regexp = "USER|ADMIN")
    @Column(nullable = false)
    private String role;

    // JSONB example field in Postgres (via Hibernate Types)
    // Requires hibernate-types dependency & column type set to jsonb
    @Type(org.hibernate.type.JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> preferences;
}
```

**Repository**

```java
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
}
```

**Service mapping DTO → Entity (prefer factories/constructors)**

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository repo;
    public UserService(UserRepository repo) { this.repo = repo; }

    @Transactional
    public UserResponse createUser(CreateUserRequest req) {
        if (repo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        var entity = UserEntity.builder()
                .email(req.getEmail())
                .displayName(req.getDisplayName())
                .role(req.getRole() == null ? "USER" : req.getRole())
                .preferences(Map.of(
                    "marketingOptIn", Optional.ofNullable(req.getPreferences())
                                              .map(CreateUserRequest.Preferences::getMarketingOptIn)
                                              .orElse(false),
                    "tags", Optional.ofNullable(req.getPreferences())
                                    .map(CreateUserRequest.Preferences::getTags)
                                    .orElse(List.of())
                ))
                .build();

        var saved = repo.save(entity);
        return new UserResponse(saved.getId(), saved.getEmail(), saved.getDisplayName(), saved.getRole());
    }
}
```

**Response DTO (Lombok optional)**

```java
public record UserResponse(Long id, String email, String displayName, String role) {}
```

---

# 5) Example: MongoDB (JSON database) with Lombok DTOs

```java
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import lombok.*;

@Document("users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDoc {
    @Id
    private String id;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 2, max = 50)
    private String displayName;

    @Pattern(regexp = "USER|ADMIN")
    private String role;

    private Map<String, Object> preferences;
}
```

**Repository**

```java
import org.springframework.data.mongodb.repository.MongoRepository;

interface UserDocRepo extends MongoRepository<UserDoc, String> {
    boolean existsByEmail(String email);
}
```

---

# 6) Example: Postgres JSONB without external library (simple string)

If you don’t want `hibernate-types`, you can store **JSONB** as `String` and parse/validate in service:

```java
@Column(columnDefinition = "jsonb")
private String preferencesJson; // store serialized JSON

// In service, use Jackson ObjectMapper to validate/parse
```

Pros: fewer deps. Cons: manual parsing/validation, less type safety.

---

# 7) Validation Deep-Dive (for React API calls)

**What to use**

* **Jakarta Bean Validation (JSR 380)** via `spring-boot-starter-validation`.
* Annotate **request DTO fields** (`@NotBlank`, `@Email`, `@Size`, `@Pattern`, `@Min`, `@Max`, `@Positive`, `@Past`, `@Valid` on nested).
* Add `@Valid` on controller parameters.
* Provide a **global exception handler** to return structured errors your React UI can render inline.

**Custom validator example**

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RoleValidator.class)
public @interface ValidRole {
    String message() default "Invalid role";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class RoleValidator implements ConstraintValidator<ValidRole, String> {
    private static final Set<String> ALLOWED = Set.of("USER","ADMIN");
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        return value != null && ALLOWED.contains(value);
    }
}
```

Use `@ValidRole` on your DTO field.

**React UX tip:** return `{ errors: [{ field, message }] }` so you can map messages to inputs.

---

# 8) When to Choose Which DB (quick guide)

| Need                                      | Best Fit                     |
| ----------------------------------------- | ---------------------------- |
| Fast local testing, ephemeral data        | **H2**                       |
| Embedded legacy constraints               | **Derby** (only if required) |
| Production OLTP, strong SQL, transactions | **PostgreSQL**               |
| Mostly relational + some flexible fields  | **PostgreSQL + JSONB**       |
| Highly document-shaped, evolving schema   | **MongoDB**                  |

---

# 9) Recommendations for your stack

* **Postgres for prod**, **H2 for tests**, avoid Derby unless mandated.
* Keep **Lombok builders on DTOs**; be intentional if using them on **entities**. Consider **factory methods** or **service methods** for entity creation.
* Use **Bean Validation** + **ControllerAdvice** to give React consistent 400s with field messages.
* If you need flexible payloads (user preferences, dynamic settings), favor **Postgres JSONB** first (keeps one DB), move to **Mongo** only if your domain is truly document-first.


Awesome—here’s a clean, copy-pasteable Docker setup for **PostgreSQL + your Spring Boot API**, with profiles that let you keep **H2 for tests** and **Postgres for dev/prod**. I also included a tiny **React form** that calls your `/api/users` endpoint and renders validation errors.

---

# 1) Docker Compose (Postgres + API)

**File:** `docker-compose.yml`

```yaml
services:
  db:
    image: postgres:16-alpine
    container_name: app_db
    restart: unless-stopped
    environment:
      POSTGRES_DB: appdb
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    ports:
      - "5432:5432"
    volumes:
      - db_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U app -d appdb"]
      interval: 5s
      timeout: 3s
      retries: 10

  api:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: app_api
    depends_on:
      db:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/appdb
      SPRING_DATASOURCE_USERNAME: app
      SPRING_DATASOURCE_PASSWORD: secret
      # Optional: Hibernate DDL for local dev (use validate in prod)
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    ports:
      - "8080:8080"
    restart: unless-stopped

volumes:
  db_data:
```

---

# 2) Spring Boot App Dockerfile

**File:** `Dockerfile`

```dockerfile
# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src
RUN ./gradlew clean bootJar --no-daemon

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

> If you use Maven, swap the build step to `mvn -q -DskipTests clean package` and copy `target/*.jar`.

---

# 3) Spring Profiles

**File:** `src/main/resources/application.yml`

```yaml
spring:
  application:
    name: app

# Default profile: good for local dev with H2 if you run without docker
---
spring:
  config:
    activate:
      on-profile: default
  datasource:
    url: jdbc:h2:mem:appdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    open-in-view: false

# Docker profile: points to Postgres container
---
spring:
  config:
    activate:
      on-profile: docker
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: ${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
    open-in-view: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

**File (tests):** `src/test/resources/application-test.yml`

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
```

> Run tests with `-Dspring.profiles.active=test` (Maven) or `-Psomething` if you prefer; with Spring Boot + Gradle, tests can pick this up via `@ActiveProfiles("test")` on your test classes.

---

# 4) Minimal API pieces (matches earlier DTO/validation)

**Request DTO (builder + validation):**

```java
// src/main/java/com/example/users/CreateUserRequest.java
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class CreateUserRequest {
    @NotBlank @Email String email;
    @NotBlank @Size(min = 2, max = 50) String displayName;
    @Pattern(regexp = "USER|ADMIN", message = "role must be USER or ADMIN")
    String role;

    @Valid
    Preferences preferences;

    @Value @Builder
    public static class Preferences {
        @NotNull Boolean marketingOptIn;
        @Size(max = 3) List<@NotBlank String> tags;
    }
}
```

**Entity (Postgres):**

```java
// src/main/java/com/example/users/UserEntity.java
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Email
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String displayName;

    @Pattern(regexp = "USER|ADMIN")
    @Column(nullable = false)
    private String role;

    // keep it simple: store JSON as text; upgrade to jsonb type if you add hibernate-types
    @Column(columnDefinition = "jsonb")
    private String preferencesJson;
}
```

**Repository + Service:**

```java
// src/main/java/com/example/users/UserRepository.java
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
}
```

```java
// src/main/java/com/example/users/UserService.java
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository repo;
    private final ObjectMapper mapper;

    public UserService(UserRepository repo, ObjectMapper mapper) {
        this.repo = repo; this.mapper = mapper;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest req) {
        if (repo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        String preferencesJson = "{}";
        try {
            preferencesJson = mapper.writeValueAsString(req.getPreferences());
        } catch (Exception ignored) {}

        var saved = repo.save(UserEntity.builder()
                .email(req.getEmail())
                .displayName(req.getDisplayName())
                .role(req.getRole() == null ? "USER" : req.getRole())
                .preferencesJson(preferencesJson)
                .build());

        return new UserResponse(saved.getId(), saved.getEmail(), saved.getDisplayName(), saved.getRole());
    }
}
```

**Controller + Validation error shape:**

```java
// src/main/java/com/example/users/UserController.java
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService svc;
    public UserController(UserService svc) { this.svc = svc; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest req) {
        return svc.createUser(req);
    }
}
```

```java
// src/main/java/com/example/common/ApiExceptionHandler.java
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("title","Validation Failed","status",400,"errors",errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("title","Conflict","status",409,"errors",List.of(Map.of("message", ex.getMessage()))));
    }
}
```

**Response DTO:**

```java
// src/main/java/com/example/users/UserResponse.java
public record UserResponse(Long id, String email, String displayName, String role) {}
```

---

# 5) React Form (Vite) calling the API + showing errors

**.env.local (Vite)**

```
VITE_API_BASE_URL=http://localhost:8080
```

**Form component (TypeScript/React):**

```tsx
// src/components/CreateUserForm.tsx
import { useState } from "react";

type ErrorItem = { field?: string; message: string };

export default function CreateUserForm() {
  const [email, setEmail] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [role, setRole] = useState("USER");
  const [marketingOptIn, setMarketingOptIn] = useState(true);
  const [tags, setTags] = useState<string>("help,team"); // comma-separated
  const [errors, setErrors] = useState<ErrorItem[]>([]);
  const [created, setCreated] = useState<any>(null);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors([]);
    setCreated(null);
    const payload = {
      email,
      displayName,
      role,
      preferences: {
        marketingOptIn,
        tags: tags.split(",").map(t => t.trim()).filter(Boolean).slice(0, 3),
      },
    };
    try {
      const res = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/users`, {
        method: "POST",
        headers: {"Content-Type":"application/json"},
        body: JSON.stringify(payload),
      });
      const data = await res.json();
      if (!res.ok) {
        setErrors((data.errors ?? []).map((e: any) => ({ field: e.field, message: e.message ?? e })));
      } else {
        setCreated(data);
        setEmail(""); setDisplayName(""); setRole("USER"); setMarketingOptIn(true); setTags("");
      }
    } catch (err: any) {
      setErrors([{ message: err.message }]);
    }
  };

  const fieldError = (name: string) =>
    errors.find(e => e.field === name)?.message;

  return (
    <form onSubmit={submit} className="max-w-md space-y-4">
      <div>
        <label className="block font-medium">Email</label>
        <input className="border p-2 w-full" value={email} onChange={e=>setEmail(e.target.value)} />
        {fieldError("email") && <p className="text-red-600 text-sm">{fieldError("email")}</p>}
      </div>
      <div>
        <label className="block font-medium">Display Name</label>
        <input className="border p-2 w-full" value={displayName} onChange={e=>setDisplayName(e.target.value)} />
        {fieldError("displayName") && <p className="text-red-600 text-sm">{fieldError("displayName")}</p>}
      </div>
      <div>
        <label className="block font-medium">Role</label>
        <select className="border p-2 w-full" value={role} onChange={e=>setRole(e.target.value)}>
          <option>USER</option>
          <option>ADMIN</option>
        </select>
        {fieldError("role") && <p className="text-red-600 text-sm">{fieldError("role")}</p>}
      </div>
      <div className="flex items-center gap-2">
        <input id="mo" type="checkbox" checked={marketingOptIn} onChange={()=>setMarketingOptIn(v=>!v)} />
        <label htmlFor="mo">Marketing Opt In</label>
      </div>
      <div>
        <label className="block font-medium">Tags (comma-separated, max 3)</label>
        <input className="border p-2 w-full" value={tags} onChange={e=>setTags(e.target.value)} />
      </div>
      <button className="bg-black text-white px-4 py-2 rounded">Create</button>

      {!!errors.length && (
        <div className="bg-red-50 border border-red-200 p-3 rounded">
          <p className="font-semibold mb-1">Errors</p>
          <ul className="list-disc ml-5">
            {errors.map((e, i) => <li key={i}>{e.field ? `${e.field}: ` : ""}{e.message}</li>)}
          </ul>
        </div>
      )}
      {created && (
        <div className="bg-green-50 border border-green-200 p-3 rounded">
          <p className="font-semibold">Created!</p>
          <pre className="text-xs overflow-auto">{JSON.stringify(created, null, 2)}</pre>
        </div>
      )}
    </form>
  );
}
```

---

# 6) Run it

```bash
# 1) Build your Spring JAR and Docker image
docker compose build

# 2) Launch Postgres + API
docker compose up -d

# 3) Verify
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"bad","displayName":"","role":"ROOT"}'
# => HTTP 400 with field-level errors
```

---

# 7) Notes & Options

* For **JSONB with typed mapping**, add `com.vladmihalcea:hibernate-types-60` and change `preferencesJson` to a `Map<String,Object>` with `@Type(org.hibernate.type.JsonType.class)`.
* Keep **builders on DTOs**; for entities, keep **no-args ctor** and be mindful of equals/hashCode.
* In prod, set `ddl-auto: validate` and use migrations (Liquibase/Flyway).
* If you want the **React app in the same compose**, add a `web` service that builds your Vite app and serves it (or run `npm run dev` separately during development).

Perfect—here’s a tight **Nginx “web”** layer that serves your built React app and **reverse-proxies `/api` → Spring** in Docker. Copy these files as-is.

---

# 1) docker-compose.yml (db + api + web/nginx)

```yaml
services:
  db:
    image: postgres:16-alpine
    container_name: app_db
    restart: unless-stopped
    environment:
      POSTGRES_DB: appdb
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    ports: ["5432:5432"]
    volumes: [db_data:/var/lib/postgresql/data]
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U app -d appdb"]
      interval: 5s
      timeout: 3s
      retries: 10

  api:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: app_api
    depends_on:
      db:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/appdb
      SPRING_DATASOURCE_USERNAME: app
      SPRING_DATASOURCE_PASSWORD: secret
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    expose: ["8080"]        # internal to compose
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "wget -qO- http://localhost:8080/actuator/health | grep UP || exit 1"]
      interval: 10s
      timeout: 3s
      retries: 10

  web:
    build:
      context: ./web
      dockerfile: Dockerfile.web
    container_name: app_web
    depends_on:
      api:
        condition: service_healthy
    ports: ["80:80"]
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "wget -qO- http://localhost/healthz || exit 1"]
      interval: 10s
      timeout: 3s
      retries: 10

volumes:
  db_data:
```

> The `web` service serves the React build and proxies `/api/*` to `api:8080`—no CORS headaches.

---

# 2) Nginx + React (multi-stage) – `web/Dockerfile.web`

```dockerfile
# --- Build the React app ---
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json package-lock.json* yarn.lock* ./
RUN npm ci || yarn --frozen-lockfile
COPY . .
# Ensure the app fetches the API via relative path (/api)
# (no need for VITE_API_BASE_URL; we'll call /api directly)
RUN npm run build

# --- Serve with Nginx ---
FROM nginx:alpine
# Copy app
COPY --from=build /app/dist /usr/share/nginx/html
# Nginx config (proxy /api to Spring)
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Tiny alive endpoint for healthcheck
RUN printf 'OK' > /usr/share/nginx/html/healthz
EXPOSE 80
CMD ["nginx","-g","daemon off;"]
```

---

# 3) Nginx config – `web/nginx.conf`

```nginx
server {
  listen 80;
  server_name _;

  # Serve React build
  root /usr/share/nginx/html;
  index index.html;

  # Gzip for text assets
  gzip on;
  gzip_types text/plain text/css application/javascript application/json image/svg+xml;
  gzip_min_length 1024;

  # Security-ish headers (adjust as needed)
  add_header X-Content-Type-Options nosniff;
  add_header X-Frame-Options SAMEORIGIN;
  add_header Referrer-Policy no-referrer-when-downgrade;

  # Static cache
  location ~* \.(?:js|css|svg|woff2?)$ {
    expires 7d;
    access_log off;
    try_files $uri =404;
  }

  # Healthcheck
  location = /healthz { try_files /healthz =200; }

  # API proxy → Spring
  location /api/ {
    proxy_pass         http://api:8080/api/;
    proxy_http_version 1.1;
    proxy_set_header   Host $host;
    proxy_set_header   X-Real-IP $remote_addr;
    proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header   X-Forwarded-Proto $scheme;
    proxy_connect_timeout 5s;
    proxy_read_timeout 60s;
  }

  # SPA fallback (React Router)
  location / {
    try_files $uri /index.html;
  }
}
```

---

# 4) React: call the API with a relative path (no CORS)

Update your fetch in the form to **use `/api`** (drop the Vite env var):

```ts
const res = await fetch(`/api/users`, {
  method: "POST",
  headers: {"Content-Type":"application/json"},
  body: JSON.stringify(payload),
});
```

No `.env` needed for the web container.

---

# 5) Spring Boot Actuator (optional but recommended)

Add Actuator so the API healthcheck works:

```gradle
dependencies {
  implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```

`application.yml` (any profile):

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
```

---

# 6) Local dev vs Docker

* **Full Docker (recommended for team parity)**

  ```bash
  docker compose build
  docker compose up -d
  # App: http://localhost
  # API (direct): http://localhost/api/users   (proxied)
  ```
* **Local dev (no Docker)**

  * Run Spring on `:8080`
  * Run Vite on `:5173` with **dev proxy** in `vite.config.ts`:

    ```ts
    export default defineConfig({
      server: {
        proxy: { '/api': 'http://localhost:8080' }
      }
    })
    ```
  * Now the browser loads from `http://localhost:5173`, `/api/*` forwards to Spring.

---

# 7) Optional: tighten JSONB + headers later

When you’re ready:

* Swap `preferencesJson` to `Map<String,Object>` using `hibernate-types` and `@Type(JsonType.class)`.
* Add `Content-Security-Policy` once you know the exact origins (Keycloak, CDN, etc.).
* Add `client_max_body_size` if you expect uploads.

---

