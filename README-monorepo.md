# Blog Monorepo

This is a monorepo containing both the backend (Kotlin/Spring Boot) and frontend (Next.js) for the blog application.

## Project Structure

```
blog-monorepo/
├── backend/                 # Kotlin Spring Boot API
│   ├── build.gradle.kts
│   ├── src/
│   └── ...
├── frontend/               # Next.js React application
│   ├── package.json
│   ├── src/
│   └── ...
├── shared/                 # Shared resources
│   ├── api-spec/          # OpenAPI specifications
│   └── types/             # Shared type definitions
├── scripts/               # Development and build scripts
├── docker-compose.yml     # Docker setup
├── package.json          # Root package.json for scripts
└── README.md
```

## Getting Started

### Prerequisites

- Node.js 18+
- JDK 17+
- Docker (optional)

### Development

```bash
# Install dependencies
npm install

# Start both backend and frontend in development mode
npm run dev

# Start backend only
npm run dev:backend

# Start frontend only
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

# Run backend tests only
npm run test:backend

# Run frontend tests only
npm run test:frontend
```

### Docker

```bash
# Start all services with Docker Compose
npm run docker:up

# Stop all services
npm run docker:down
```

## Services

- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:3000
- **Database**: PostgreSQL on port 5432