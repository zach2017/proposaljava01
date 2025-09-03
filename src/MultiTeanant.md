
# What “multi-tenant” means (quick decisions)

1. **Tenant identification (how we know who the tenant is)**
   Pick one (you can support several):

   * **Subdomain**: `acme.yourapp.com` → `tenant = "acme"`.
   * **Path prefix**: `/t/acme/...`.
   * **Header**: `X-Tenant-ID: acme` (good for internal services).
   * **JWT claim**: `tenant_id` in the access token (Keycloak-friendly).

2. **Data isolation model (how data is separated)**
   Choose one (recommended order for MVP → scale):

   * **Row-level security (RLS) in Postgres**: single DB, `tenant_id` column on every table + Postgres RLS policies. **(Best balance: strong isolation, simple ops)**
   * **Schema-per-tenant**: one schema per tenant (Hibernate multi-tenancy SCHEMA).
   * **Database-per-tenant**: strongest isolation, highest ops cost (migrations, connections).

3. **Identity**

   * Keycloak: add a **`tenant_id` claim** (via protocol mapper or groups/attributes).
   * One realm for all tenants (simpler) or broker tenant IdPs if needed.

4. **Frontend**

   * Serve one static build for all tenants; at runtime, fetch **tenant config** (logo, colors, name, features) via `/api/tenants/me/config` (or by subdomain) and theme the app.

---

# Implement now (MVP checklist)

1. **Create a stable tenant ID scheme** (e.g., lowercase slug; disallow special chars).
2. **Add a TenantContext + Resolver Filter** (subdomain/header/JWT) to capture the tenant for each request.
3. **Add `tenant_id` to all tables** + **Postgres RLS policies** (recommended).
4. **Enforce tenant on every transaction** (set a Postgres session var via `SET LOCAL`).
5. **Map Keycloak → JWT `tenant_id` claim**; in Spring Security extract it automatically.
6. **Add a `/api/tenants/me/config` endpoint** used by React on app start; cache per tenant.
7. **Observability**: include `tenant_id` in logs/metrics and cache keys.
8. **Flyway/Liquibase**: write “global” migrations + tenant bootstrap scripts.
9. **Backups & S3/Blob**: store files under `tenant/{id}/...`.
10. **Tests**: two tenants, same endpoint, data must not leak.

---

# Backend: minimal code to start (RLS approach)

## 1) Tenant context & resolver

```java
// TenantContext.java
public final class TenantContext {
  private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();
  public static void set(String id) { CURRENT.set(id); }
  public static String get() { return CURRENT.get(); }
  public static void clear() { CURRENT.remove(); }
}
```

```java
// TenantResolverFilter.java
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TenantResolverFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, java.io.IOException {
    try {
      String tenant = resolveFromHost(req.getServerName());
      if (tenant == null) tenant = req.getHeader("X-Tenant-ID");

      var principal = req.getUserPrincipal();
      if (tenant == null && principal instanceof JwtAuthenticationToken jwt) {
        Object claim = jwt.getToken().getClaim("tenant_id");
        if (claim != null) tenant = claim.toString();
      }

      if (tenant == null) {
        res.sendError(400, "Tenant not resolved"); // or default/anon tenant
        return;
      }
      TenantContext.set(tenant);
      chain.doFilter(req, res);
    } finally {
      TenantContext.clear();
    }
  }

  private String resolveFromHost(String host) {
    // acme.app.com -> "acme"
    if (host == null) return null;
    String[] parts = host.split("\\.");
    return parts.length > 2 ? parts[0] : null; // adjust for your DNS pattern
  }
}
```

## 2) Postgres RLS & session variable

**Add column + policy** (Flyway migration):

```sql
-- V1__add_tenant_and_rls.sql
ALTER TABLE users ADD COLUMN IF NOT EXISTS tenant_id text NOT NULL;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS tenant_id text NOT NULL;

-- Enable RLS
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE tasks ENABLE ROW LEVEL SECURITY;

-- Session GUC: current_setting('app.tenant', true)
CREATE POLICY users_tenant_isolation ON users
  USING (tenant_id = current_setting('app.tenant', true));
CREATE POLICY tasks_tenant_isolation ON tasks
  USING (tenant_id = current_setting('app.tenant', true));
```

**Ensure inserts set tenant\_id** (via app code or DB default):

```sql
-- Optional: trigger to default tenant_id from GUC if not provided
CREATE OR REPLACE FUNCTION set_tenant_id_default()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  IF NEW.tenant_id IS NULL THEN
    NEW.tenant_id := current_setting('app.tenant', true);
  END IF;
  RETURN NEW;
END $$;

CREATE TRIGGER users_tenant_default BEFORE INSERT ON users
FOR EACH ROW EXECUTE FUNCTION set_tenant_id_default();

CREATE TRIGGER tasks_tenant_default BEFORE INSERT ON tasks
FOR EACH ROW EXECUTE FUNCTION set_tenant_id_default();
```

**Set the session variable per request/transaction**:

```java
// TenantSessionVarConfigurer.java
import javax.sql.DataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import jakarta.annotation.PostConstruct;

@Configuration
public class TenantSessionVarConfigurer {

  private final DataSource dataSource;

  public TenantSessionVarConfigurer(DataSource ds) { this.dataSource = ds; }

  @PostConstruct
  void registerSync() {
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override public void beforeCommit(boolean readOnly) {
        setSessionTenantVar();
      }
      @Override public void beforeCompletion() {
        // no-op
      }
      private void setSessionTenantVar() {
        String tenant = TenantContext.get();
        if (tenant == null) return;
        try (var conn = DataSourceUtils.getConnection(dataSource);
             var ps = conn.prepareStatement("SET LOCAL app.tenant = ?")) {
          ps.setString(1, tenant);
          ps.execute();
        } catch (Exception ignored) {}
      }
    });
  }
}
```

> Alternative: use a `Jpa/Hibernate Interceptor` to run `SET LOCAL app.tenant = ?` at tx begin.

**Important**: All queries must run inside a transaction so `SET LOCAL` applies. Use `@Transactional` (read-only for reads too).

## 3) Spring Security: pull tenant from JWT

```java
// SecurityConfig.java (snippet)
http.oauth2ResourceServer(oauth2 -> oauth2.jwt());
```

Then your `TenantResolverFilter` picks `tenant_id` from the token claim.

---

# Backend: schema-per-tenant (if you choose SCHEMA instead)

Add properties:

```properties
spring.jpa.properties.hibernate.multiTenancy=SCHEMA
spring.jpa.properties.hibernate.tenant_identifier_resolver=com.yourapp.tenancy.CurrentTenantResolver
spring.jpa.properties.hibernate.multi_tenant_connection_provider=com.yourapp.tenancy.SchemaConnectionProvider
```

Implement:

* `CurrentTenantResolver` → returns `TenantContext.get()`
* `SchemaConnectionProvider` → switches to tenant schema on connection (e.g., `SET search_path = tenant_xyz, public`)

You’ll need **per-schema migrations** during tenant onboarding.

---

# Frontend (Vite React) changes

1. **Boot-time config**:

```ts
// src/tenantConfig.ts
export type TenantConfig = { name: string; logoUrl: string; theme: Record<string,string>; features: string[] };

export async function loadTenantConfig(): Promise<TenantConfig> {
  const res = await fetch('/api/tenants/me/config', { credentials: 'include' });
  return await res.json();
}
```

2. **App start**: fetch config, set theme, render.
3. **Branding by tenant**: logo/theme pulled from backend (cached by tenant).

You still deploy **one static build**; multi-tenancy is **runtime**.

---

# Keycloak (recommended now)

* Add **Protocol Mapper** to include `tenant_id` claim in tokens.
* Option A (simple): store tenant\_id as a user attribute.
* Option B (flexible): map tenant from a **group** or **client role** naming convention (e.g., `tenant:acme`).
* For external IdP per tenant, use Keycloak **Identity Brokering**, set tenant on first login via a mapper.

---

# Caching, storage, and ops (do now)

* **Cache keys**: prefix with `tenant:{id}:...` (Redis, Caffeine).
* **S3/Blob**: store under `tenant/{id}/...`.
* **Logging**: add `tenant_id` to MDC.

  ```java
  import org.slf4j.MDC;
  // in filter after resolve
  MDC.put("tenant_id", tenant);
  // finally { MDC.clear(); }
  ```
* **Rate limits/quotas**: by `tenant_id`.
* **Backups**: verify you can restore a single tenant (table filter or schema).

---

# Onboarding flow (script it)

1. Create tenant record (id, name, plan).
2. (RLS) nothing more; (SCHEMA) create schema + migrate.
3. Provision S3 prefixes, API keys, and defaults.
4. Add tenant admin user in Keycloak (assign `tenant_id`).
5. Seed starter data (optional).

---

# Tests you should add immediately

* **Two-tenants isolation test**:

  * Create `tenantA` & `tenantB`.
  * Insert data under each.
  * Read with `tenantA` context; must only see A. Same for B.
* **Unauthenticated or missing tenant** → 400/401.
* **JWT tampering**: reject/ignore claim mismatch if you also accept headers.

---

# What to implement first (1–2 day sprint)

1. TenantContext + Resolver Filter (subdomain + JWT claim).
2. Add `tenant_id` column, enable Postgres RLS, create policies.
3. Ensure all repository service methods are `@Transactional` (so `SET LOCAL` applies).
4. Flyway migration for RLS + trigger defaults.
5. `/api/tenants/me/config` endpoint and React boot-time fetch.
6. Add `tenant_id` to logs/metrics and cache keys.

This gives you **real isolation now** without a heavy ops cost—and you can evolve to schema/db-per-tenant later if needed.

If you want, I can drop in **ready-to-paste** Flyway files and a tiny Spring Boot starter module (Resolver Filter + RLS session setter + config endpoint).
