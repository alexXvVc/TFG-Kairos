# Parish Celebrations

A SaaS application for parish administrators to manage all kinds of celebrations: Masses, baptisms, weddings, funerals, confirmations, first communions, and more.

Designed for end users in their 60s and 70s — the interface is intentionally simple, the backend handles all the complexity.

## Tech stack

- **Backend**: Java 21 + Spring Boot 3
- **Database**: MySQL 8 (via Docker)
- **API testing**: Postman
- **Build**: Maven

## Architecture

The project is organized by **bounded context** (Domain-Driven Design). Each context is an independent package with its own domain model, services, and API.

```
com.parish.celebrations
├── scheduling      Core: plan and book celebrations
├── records         Core: canonical sacramental books
├── participants    Core: people involved (faithful, ministers)
├── notifications   Supporting: email and SMS reminders
├── iam             Generic: authentication and roles
└── shared          Shared kernel: common value objects
```

Inside each context, the layout follows hexagonal architecture:

```
context/
├── domain          Pure business logic. No Spring, no JPA imports.
├── application     Use cases / services. Orchestrates the domain.
├── infrastructure  JPA repositories, external integrations.
└── api             REST controllers, request/response DTOs.
```

**Dependency rule**: `api` and `infrastructure` depend on `domain`, never the reverse.

## Running locally

### 1. Start the database

```bash
docker compose -f docker/docker-compose.yml up -d
```

This launches MySQL on `localhost:3306` with:

- database: `parish`
- user: `parish`
- password: `parish_dev`

### 2. Run the app

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

### 3. Test with Postman

Import `docs/postman_collection.json` into Postman to get a starter collection of endpoints.

## Roadmap

- [x] Project skeleton
- [x] Domain model for Celebration aggregate
- [ ] Persistence layer (JPA + MySQL)
- [ ] REST API for scheduling
- [ ] Sacramental records context
- [ ] Authentication
- [ ] Notification service
- [ ] Frontend (TBD)

## Documentation

- [`docs/architecture.md`](docs/architecture.md) — bounded contexts and integration patterns
- [`docs/celebration-aggregate.md`](docs/celebration-aggregate.md) — deep dive into the core aggregate
