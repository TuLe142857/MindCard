---
trigger: always_on
---

# Rule 01: Project Structure & Architecture

## 1. Architectural Philosophy: Feature-Based (Domain-Driven) Structure
To ensure maintainability and to help AI agents understand the context easily, we strongly adhere to a **Feature-Based (or Domain-Driven)** folder structure rather than a traditional layered architecture. 

This means grouping files by their business feature (domain) rather than by their technical type (e.g., putting all hooks together or all components together is an anti-pattern here).

## 2. Directory Tree
Follow this standard directory structure inside the `src/` folder:

```text
src/
├── assets/            # Static files (images, global CSS, etc.)
├── features/          # Feature domains (Core business logic)
│   ├── auth/          # Example: Authentication feature
│   │   ├── api/       # Axios API calls specific to auth
│   │   ├── components/# UI components used ONLY in auth
│   │   ├── hooks/     # React Query hooks (e.g., useLogin, useLogout)
│   │   └── types/     # TS definitions specific to auth
│   └── documents/     # Example: Documents feature
│       ├── api/       # API endpoints mapping to /documents
│       ├── components/# DocumentCard, DocumentList, etc.
│       ├── hooks/     # useDocuments, useUploadDocument (React Query)
│       └── types/     # Document model definitions based on 
├── shared/            # Shared packages and utilities used across the app
│   ├── components/    # Reusable global UI components (Button, Input, Modal, etc.)
│   ├── hooks/         # Generic React hooks (useDebounce, useWindowSize, etc.)
│   ├── utils/         # Helper functions (date formatters, string manipulators)
│   └── types/         # Global TypeScript interfaces
├── layouts/           # Page layouts (MainLayout, AuthLayout)
├── pages/             # Route components (Smart components that compose features)
├── store/             # Global Redux store configuration and global slices (e.g., userSlice)
├── router/            # React Router setup
├── App.tsx            # Main application component
└── main.tsx           # Entry point
```

## 3. Strict Rules for AI Agents
- **Rule 1.1 - Feature Isolation:** A feature folder (e.g., `features/documents`) should be completely self-contained. It contains its own API calls, types, hooks, and UI components.

- **Rule 1.2 - Cross-Feature Imports:** Features should NEVER import directly from other features' internal directories. If multiple features need the same component or logic, move it to the appropriate global folder (e.g., `src/components/` or `src/utils/`).

- **Rule 1.3 - Shared UI:** All generic UI components (styled with Tailwind and Lucide icons) like generic Buttons, Inputs, and Dialogs must be placed in `src/components/`.

- **Rule 1.4 - Pages vs Features:** The `pages/` directory should only contain lightweight components that represent routes. A Page component's main job is to use Layouts and compose components imported from the `features/` directory. Do not put heavy business logic inside `pages/`.

- **Rule 1.5 - Absolute Imports:** Always use absolute imports configured in `tsconfig.json` (e.g., `import { useDocuments } from '@/features/documents/hooks'` instead of relative paths like `../../../`) to keep imports clean and refactor-friendly.