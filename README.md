# PharmaSave SmartCart

PharmaSave is a secure full-stack price aggregator for OTC medicines and cosmetics. It ingests weekly supermarket promotions, validates the Thursday 00:00:00 to Wednesday 23:59:59 cycle, ranks matched SKUs by lowest price and longest shelf life, and uses authenticated customer behavior to personalize the homepage hero banner.

## Assignment Coverage

- Weeks 1-2: Spring Boot, React, PostgreSQL, Docker Compose, normalized SmartCart schema, scheduled promotional ingestion, JWT login and registration.
- Weeks 3-4: Cross-supermarket SKU matching, lowest-price then shelf-life ranking, protected telemetry APIs, sub-category affinity scoring.
- Weeks 5-6: Responsive React TypeScript UI, guarded authenticated dashboard, Authorization bearer sync, personalized hero promotion banner.
- Weeks 7-8: Unit tests for JWT, promo windows, and deal tie-breaks, indexed database tables, local container orchestration.

## Run Locally

```bash
docker-compose up --build
```

Frontend: http://localhost:5173

Backend health: http://localhost:8080/api/health

Demo login:

- Username: `demo`
- Password: `Demo@123`

## Key Endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/recommendations/hero`
- `GET /api/promotions/best`
- `POST /api/promotions/ingest`
- `POST /api/telemetry/interactions`
