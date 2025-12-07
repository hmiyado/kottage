# Kottage Monorepo

A full-stack blog application with Kotlin backend and Next.js frontend.

## Project Structure

```
kottage/
├── backend/                 # Kotlin Ktor API (kottage-server)
│   ├── build.gradle.kts
│   ├── src/
│   └── ...
├── frontend/               # Next.js React application (kottage-front)
│   ├── package.json
│   ├── src/
│   └── ...
├── shared/                 # Shared resources
│   ├── api-spec/          # OpenAPI specifications
│   └── types/             # Shared type definitions
├── docker-compose.yml     # Docker setup
├── package.json          # Root package.json for scripts
└── README.md
```

## Technology Stack

### Backend (Kotlin)
- **Ktor 3.2.3**: Web framework with Netty server
- **Exposed**: Database ORM with flyway migrations
- **Koin**: Dependency injection
- **TiDB Serverless**: Primary database (MySQL-compatible)
- **Redis**: Session storage
- **Karate**: E2E API testing

### Frontend (Next.js)
- **Next.js 14**: React framework with App Router
- **TypeScript**: Type safety
- **Tailwind CSS**: Styling
- **Vitest**: Testing framework

## Getting Started

### Prerequisites

- Node.js 23+ (see frontend/.nvmrc)
- JDK 17+
- Docker & Docker Compose (recommended)

### Development with Docker

```bash
# Start all services (recommended)
npm run docker:up

# Stop all services
npm run docker:down
```

### Local Development

```bash
# Install dependencies
npm install

# Start both backend and frontend
npm run dev

# Start backend only (requires Java 17+)
npm run dev:backend

# Start frontend only (requires Node.js 23+)
npm run dev:frontend
```

### Building

```bash
# Build both projects
npm run build

# Build backend only
npm run build:backend

# Build frontend only
npm run build:frontend
```

### Testing

```bash
# Run all tests
npm test

# Run backend tests
npm run test:backend

# Run frontend tests
npm run test:frontend
```

### Code Quality

```bash
# Lint all code
npm run lint

# Format all code
npm run format
```

## Services

When running with Docker Compose:

- **Backend API**: http://localhost:8080
- **Frontend**: http://localhost:3000
- **MySQL (dev)**: localhost:3306
- **Redis**: localhost:6379

**Production:**
- **Database**: TiDB Serverless (cloud-hosted)

## Development Notes

- The backend uses Kotlin with Ktor framework (not Spring Boot)
- The frontend is built with Next.js and TypeScript
- Authentication supports Google OIDC and admin basic auth
- API specifications are defined using OpenAPI
- Database migrations are handled by Flyway

For detailed documentation:
- Backend: See `backend/README.md` and `backend/CLAUDE.md`
- Frontend: See `frontend/README.md` and `frontend/CLAUDE.md`