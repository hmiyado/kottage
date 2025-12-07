# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Running the Application

```sh
# Run locally on http://0.0.0.0:8080
./gradlew run

# Run with auto-reload (requires two terminals)
./gradlew -t build    # Terminal 1: continuous build
./gradlew run         # Terminal 2: run server

# Run in Docker container
cp .ci.db-env .db-env && cp .ci.env .env
sh ./scripts/run-on-docker.sh
```

### Testing

```sh
# All tests
./gradlew test

# Unit tests only (excludes e2e karate tests)
./gradlew test -x karate:test

# E2e tests (requires running server)
./gradlew karate:test

# Debug single karate test
./gradlew karate:karateDebug
```

### Code Quality

```sh
# Lint with ktlint
./gradlew ktlintCheck

# Format code
./gradlew ktlintFormat
```

### Deployment

```sh
# Build and publish Docker image
sh ./scripts/publish-docker-image.sh

# Deploy infrastructure (from infra/ directory)
cd infra && make plan    # Preview changes
cd infra && make apply   # Deploy changes
```

## Architecture Overview

This is a **Kotlin web application** built with **Ktor framework** for a blog/content management system called "
kottage". The application follows a layered architecture with dependency injection via Koin.

### Core Technologies

- **Ktor 3.2.3**: Web framework with Netty server
- **Exposed**: Database ORM with flyway migrations
- **Koin**: Dependency injection
- **kotlinx.serialization**: JSON serialization
- **Redis**: Session storage
- **TiDB Serverless**: Primary database (MySQL-compatible, production)
- **MySQL**: Local development database
- **Karate**: E2E API testing
- **Kotest**: Unit testing framework

### Application Structure

**Application Layer** (`src/main/kotlin/com/github/hmiyado/kottage/application/`):

- `Application.kt`: Main application configuration and plugin setup
- `plugins/`: Ktor plugins (authentication, CORS, sessions, CSRF protection, etc.)
- `configuration/`: Environment and database configuration modules

**Domain Models** (`src/main/kotlin/com/github/hmiyado/kottage/model/`):

- Core entities: `Entry.kt`, `Comment.kt`, `User.kt`, `Health.kt`
- Validation and serialization logic

**Repository Layer** (`src/main/kotlin/com/github/hmiyado/kottage/repository/`):

- Database abstractions with both in-memory and database implementations
- Exposed table definitions and migration scripts
- Separate repositories for entries, comments, users, and OAuth

**Service Layer** (`src/main/kotlin/com/github/hmiyado/kottage/service/`):

- Business logic for entries, comments, users, and health checks
- Password generation and user management

**Route Layer** (`src/main/kotlin/com/github/hmiyado/kottage/route/`):

- REST API endpoints following OpenAPI specifications
- Location classes define URL structures and request/response handling
- Separate route modules for entries, comments, users, OAuth, and health

**Authentication & Authorization**:

- Multi-provider auth: Admin basic auth, user sessions, Google OIDC
- CSRF protection with custom tokens
- Role-based access (admin vs regular users)

**Infrastructure**:

- Terraform configuration in `infra/` for AWS deployment
- Lambda functions for HTTP proxy and log parsing
- Docker support with health checks

**Code Generation**:

- OpenAPI spec-driven server code generation
- Build configuration templates
- Custom Mustache templates for API generation

### Key Configuration

- Main config: `src/main/resources/application.conf`
- Environment variables for database, Redis, OAuth, webhooks
- Development vs Production environment handling
- Flyway database migrations in `src/main/resources/db/migration/`

### Testing Strategy

- Unit tests with Kotest and Mockk
- E2E API tests with Karate framework
- Separate test configurations and resources
- Integration tests with in-memory repositories
