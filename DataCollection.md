# RFP Intake & Data Engineering Checklist
*Complex App Modernization → ReactJS + Java Spring (AWS), Secure Login (OIDC/MFA), Real‑Time Data, Large‑Scale Analytics*

**RFP/Deal Name:** __________________  **Owner:** __________________  **Due:** __________

---

## 1) Kickoff & Source Inventory
- [ ] Identify all sources: Excel/Sheets, resumes (PDF/DOCX), HR/ATS, CRM, PMO tools, Confluence/Jira/Git, certification portals, partner lists.
- [ ] Name data owners & refresh cadence; record access paths and permissions.
- [ ] Capture buyer context (industry, compliance: HIPAA/PCI, data residency, uptime SLA, timeline).

## 2) Access & Raw Capture
- [ ] Establish read‑only access (views/exports); set up `/intake/raw` with date‑stamped drops.
- [ ] Export Excel/Sheets → CSV; store resumes original + hash; snapshot DB views.
- [ ] Log source metadata (owner, last_updated, refresh frequency).

## 3) Canonical Schemas (define “gold” fields)
- [ ] Requirements: id | text | priority(must/should/could) | weight | domain(UI/Auth/Streaming/Analytics/AWS).
- [ ] Eval Criteria: criterion | weight | rubric | pass/fail.
- [ ] Employee: id | role | location | clearance | availability_start | rate_band.
- [ ] Skills: name | taxonomy_path | proficiency(1–5) | last_used(YYYY‑MM).
- [ ] Certs: issuer | level | id | expires_on | verification_url | status(current/planned).
- [ ] Projects: title | industry | summary | dates | tech_stack | metrics(before/after, unit, period) | reference_contact.
- [ ] Capabilities/Partners: capability | maturity | evidence_links | rates | NDA.

## 4) Intake Templates Issued (for SMEs)
- [ ] Employees tab; Skills tab; Certs tab; Projects tab; Capabilities tab; Partners tab.
- [ ] Add dropdowns/validations; include a “DataDictionary” tab; forbid merged cells.

## 5) Ingest & Parse
- [ ] Load CSVs with header checks; record row counts & rejects.
- [ ] Parse resumes → JSON (roles, dates, skills, certs, projects); normalize dates.
- [ ] Land curated DB views (employees, assignments, projects, costs, references).

## 6) Standardize & Clean
- [ ] Apply taxonomies & synonym maps (React/React.js → React; OIDC/MFA/SSO → Identity&Access; Synapse/SQL DW → Azure Synapse).
- [ ] Normalize units (ISO dates, %s, currency); trim/uppercase codes; remove null‑like values.

## 7) Entity Resolution & IDs
- [ ] Dedupe people (email + name + tenure); retain highest proficiency & most recent last_used.
- [ ] Unify projects (title + client + dates); link contributors.
- [ ] Assign stable GUIDs for Employee, Project, Skill, Requirement.

## 8) Evidence & Proof Points
- [ ] Link metrics to artifacts (dashboards, acceptance letters, scorecards, Credly badges).
- [ ] Validate URLs (reachable, permissioned); capture proof dates & owners.

## 9) Coverage Mapping to RFP
- [ ] For each requirement, select: employees, projects, capabilities/partners, **and** evidence.
- [ ] Materialize `CoverageMatrix` (requirement_id → covered_by + coverage_level + evidence_ref).

## 10) Gap Analysis & What‑Ifs
- [ ] Identify gaps (e.g., Kinesis Data Analytics, ServiceNow integration, FedRAMP docs).
- [ ] Propose mitigations (partner, training, hire) with dates, owners, and cost deltas.

## 11) Fit Scoring & Selection
- [ ] Compute weighted coverage (penalize unmet musts); add past‑performance similarity & availability.
- [ ] Pick top 3 case studies with quantified outcomes & contactable references.

## 12) Publish to Proposal Template (bindings)
- [ ] Generate `bindings.json` for: Exec Summary, Compliance Matrix, Team, Architecture, Migration Plan, Security, SRE & SLAs, Data & Analytics, Risks, Timeline.
- [ ] Verify no empty placeholders; ensure date/currency formatting rules.

## 13) Quality Gates (pre‑handoff)
- [ ] ≥95% required fields complete; duplicates <1%.
- [ ] No expired/expiring (≤60d) certs on key roles; availability aligned to timeline.
- [ ] Evidence links valid; key skills last_used ≤18 months.
- [ ] SME spot‑checks: React, Spring, AWS, Security/OIDC, Streaming, Analytics.

## 14) Governance, Security, Privacy
- [ ] Version all intake files & RFP/Q&A addenda; maintain audit log.
- [ ] PII minimization; encrypt at rest; RBAC on evidence and resumes.
- [ ] Record template version used for generation.

## 15) Automate Refresh
- [ ] Schedule nightly/weekly syncs from HR/ATS, CRM, PMO; rerun scoring & gaps.
- [ ] Alerts for stale data (skills >18m unused, certs expiring, availability slips).

---

## Source‑Specific Quick Checks
**Excel/Sheets**
- [ ] Headers match templates; no merged cells; one record per row; `last_updated` filled.

**Resumes**
- [ ] Original + parsed JSON stored; skills tagged to taxonomy; extract certs (issuer/id/expiry).

**Business Databases**
- [ ] Read‑only views created; unnecessary PII excluded/masked; snapshots timestamped.

**Knowledge Stores (Confluence/Jira/Git)**
- [ ] Link reusable assets: React library, Spring scaffolds, Terraform modules, CI/CD pipelines, runbooks.

---

## Folder Structure (init)
```
/intake/{raw,processed/{bronze,silver,gold}}  /evidence  /rfp/<id>/{docs,qa,versions}  /models  /outputs/<id>/bindings.json
```

---

## Acceptance Criteria (Proposal‑Ready)
- [ ] Every **must‑have** mapped to ≥1 employee + project + capability/partner with evidence.
- [ ] Named team availability confirmed; timeline feasible.
- [ ] 3 case studies with metrics and references included.
- [ ] Security/compliance artifacts attached (OIDC/MFA, audit, data handling).
- [ ] Gap plan documented with cost/schedule impact.
- [ ] Proposal renders end‑to‑end from template with no empty sections.

