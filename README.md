# AI Industry Configuration Engine

A Spring Boot service that manages industry-specific configuration for multi-tenant SaaS products. Each tenant inherits the default settings of its industry (compliance, data retention, feature flags) and can override them. If a tenant signs up without an industry, a Python AI classifier detects it from the tenant's name and description.

```
client ──REST──▶ Spring Boot (:8080) ──▶ PostgreSQL (industry profiles + tenants, JSONB configs)
                       │
                       └──POST /classify──▶ Python classifier (:8000, TF-IDF + logistic regression)
```

## API

| Method | Path | What |
|---|---|---|
| `GET` | `/api/industries` | list industry profiles and their defaults |
| `GET` | `/api/industries/{industry}` | one profile |
| `PUT` | `/api/industries/{industry}/defaults` | replace an industry's defaults |
| `POST` | `/api/tenants` | create a tenant (`industry` optional: auto-detected if omitted) |
| `GET` | `/api/tenants/{id}` | tenant details, including how the industry was set |
| `PUT` | `/api/tenants/{id}/industry` | set the industry manually |
| `GET` | `/api/tenants/{id}/config` | effective config: industry defaults deep-merged with tenant overrides |
| `PUT` | `/api/tenants/{id}/config` | replace the tenant's overrides |

Starter industries (seeded on first boot): `healthcare`, `finance`, `retail`, `education`, `manufacturing`, `other`. If the classifier is unreachable or its confidence is below `classifier.min-confidence`, the tenant falls back to `other`.

## Run

With Docker:

```bash
docker compose up --build
```

Or locally (Java 17, Maven, Python 3.10+, a running PostgreSQL with database `industry_config`):

```bash
pip install -r classifier/requirements.txt
(cd classifier && uvicorn app:app --port 8000) &
mvn spring-boot:run      # DB_URL, DB_USER, DB_PASSWORD, CLASSIFIER_URL override the defaults
```

## Example

```bash
curl -XPOST localhost:8080/api/tenants -H 'content-type: application/json' \
  -d '{"name":"MediTrack","description":"Clinic scheduling and patient health records"}'
# -> "industry":"healthcare","industrySource":"classifier","industryConfidence":0.77

curl -XPUT localhost:8080/api/tenants/1/config -H 'content-type: application/json' \
  -d '{"dataRetentionDays":3650,"features":{"billing":false}}'

curl localhost:8080/api/tenants/1/config
# -> {"compliance":["HIPAA"],"auditLog":true,"dataRetentionDays":3650,
#     "features":{"patientPortal":true,"billing":false}}
```

## Status

Early skeleton: schema managed by Hibernate `ddl-auto: update` (no migrations yet), no auth, and the classifier is trained on a small built-in seed set.
