# kottage-front

frontend of [kottage](https://github.com/hmiyado/kottage).

## Initialize

```bash
git clone https://github.com/hmiyado/kottage-front.git
cd kottage-front
yarn
```

## Development

```bash
yarn dev
```

## Linting

```bash
pnpm lint
```

Linting is provided by [Oxlint](https://oxc.rs/). Configuration lives in
`.oxlintrc.json`. The active rules, severities and options mirror the previous
ESLint setup (`eslint-config-next` core-web-vitals + typescript, with
`eslint-config-prettier`). Formatting is still handled by Prettier, which is not
part of Oxlint.

### Rule coverage

Oxlint's native `nextjs`, `react`, `import`, `jsx-a11y` and `typescript`
plugins cover 79 of the 87 previously active rules. The remaining rules are
handled as follows:

- `react/jsx-uses-react` and `react/jsx-uses-vars` are unnecessary with the
  automatic JSX runtime and were dropped.
- `react-hooks/component-hook-factories` was folded into
  `react/static-components`, which is still enabled.
- `react-hooks/config` and `react-hooks/gating` configured React Compiler
  options that Oxlint now fixes internally.
- `react/require-render-return` is enabled explicitly because it lives in
  Oxlint's nursery category.
- `@next/next/no-location-assign-relative-destination` and
  `react/no-deprecated` have no native implementation. They are provided by the
  `@next/eslint-plugin-next` and `eslint-plugin-react` JS plugins, loaded as the
  `next-compat` and `react-compat` aliases.

Rule options that were already the ESLint defaults (for example
`prefer-const` and `no-unused-expressions`) are omitted; the custom
`jsx-a11y/alt-text` options are preserved. The ESLint-only `import/parsers` and
`import/resolver` settings are not used by Oxlint; the single enabled import
rule does not need them.

Generated OpenAPI sources (`src/repository/openapi/generated/**`) are kept
unchanged and excluded from lint, matching the file-level `/* eslint-disable */`
header emitted by the generator. Only the hand-written avatar `<img>` keeps an
inline suppression, now named `nextjs/no-img-element`.

## TypeScript compiler and API

The frontend uses TypeScript 7 (`typescript@7.0.2`), which ships the native
`tsc` CLI; it does not expose the legacy Compiler API:

- `pnpm ts` runs `tsc --project tsconfig.dev.json` so type checking works even
  before `next dev` creates `tsconfig.json`.
- Next.js build-time type checking uses the TypeScript CLI through
  `experimental.useTypeScriptCli` in `next.config.js`, rather than the old
  TypeScript 6 compiler API.

To check production sources independently of a Next.js build:

```bash
pnpm exec tsc --project tsconfig.production.json --incremental false
```
