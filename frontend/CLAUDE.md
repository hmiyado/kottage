# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is the frontend for "kottage" - a Japanese blog platform built with Next.js 15, React 19, and TypeScript. The application uses a component-based architecture with a clear separation between pieces (atomic components), plurals (composite components), and presentation layers.

## Development Commands

### Setup

```bash
pnpm install
```

### Development

```bash
pnpm dev         # Run development server with TypeScript watching
pnpm dev:noTs    # Run development server without TypeScript checking
pnpm ts          # Run TypeScript type checking
pnpm ts --watch  # Run TypeScript type checking in watch mode
```

### Testing

```bash
pnpm test        # Run Vitest tests
```

### Building

```bash
pnpm build       # Build for production
pnpm start       # Start production server
```

### Linting

```bash
pnpm lint        # Run Oxlint
```

Linting uses [Oxlint](https://oxc.rs/) with the rules configured in
`.oxlintrc.json` (mirroring the previous `eslint-config-next` setup). The
`@next/eslint-plugin-next` and `eslint-plugin-react` packages are loaded as
Oxlint JS plugins via the `next-compat` and `react-compat` aliases to cover the
rules Oxlint does not implement natively.

### API Client Generation

```bash
pnpm generate-client    # Generate OpenAPI client from spec
pnpm regenerate-client  # Remove and regenerate OpenAPI client
```

### Component Generation

```bash
pnpm create-component   # Generate new component using Hygen templates
```

## Architecture

### Component Structure

The codebase follows a hierarchical component architecture:

- **pieces/**: Atomic components (buttons, text fields, avatars)
- **plurals/**: Composite components that combine pieces (forms, lists, menus)
- **presentation/**: High-level page components
- **template/**: Layout components (headers, footers, column layouts)

### Key Technologies

- **Next.js 16**: React framework with App Router
- **React 19**: UI library with Suspense and concurrent features
- **TypeScript 7**: Type safety with strict configuration (native `tsc` CLI)
- **Tailwind CSS**: Utility-first CSS framework with custom design tokens
- **Vitest**: Testing framework with jsdom environment
- **SWR**: Data fetching with suspense support
- **MSW**: Mock Service Worker for API mocking

### State Management

- **UserContext**: Global user state management via React Context
- **SWR**: Server state management with automatic revalidation

### API Integration

- **OpenAPI Generated Client**: Auto-generated TypeScript client from backend spec
- **KottageClient**: Singleton API client with CSRF protection and logging middleware
- **Custom Middleware**: Handles CSRF tokens and request/response logging

### Configuration Files

- **tsconfig.json**: TypeScript configuration (switches between dev/production; `pnpm ts` targets `tsconfig.dev.json` directly)
- **next.config.js**: Next.js configuration with i18n (Japanese locale) and `experimental.useTypeScriptCli`
- **tailwind.config.js**: Custom design system with primary colors and spacing
- **vitest.config.mts**: Test configuration with React and native Vite `resolve.tsconfigPaths`
- **.oxlintrc.json**: Oxlint configuration (nextjs/react/import/jsx-a11y/typescript rules)

### Backend Integration

The project includes a Git submodule (`kottage/`) containing the backend API specification. The frontend generates its TypeScript client from this specification.

### Development Workflow

1. Use different TypeScript configurations for development and production
2. Run concurrent Next.js and TypeScript checking during development
3. Generate API clients from OpenAPI specifications
4. Use Hygen templates for consistent component creation
5. Lint both CSS modules and TypeScript code

### Testing Strategy

- **Unit Tests**: Components tested with React Testing Library
- **API Mocking**: MSW for intercepting API calls
- **Setup**: Global test configuration in `src/test/setupTests.tsx`
