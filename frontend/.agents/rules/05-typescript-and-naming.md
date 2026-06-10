---
trigger: always_on
---

# Rule 05: TypeScript & Naming Conventions

## 1. Strict TypeScript Rules
To maintain a robust and predictable codebase, AI agents must strictly adhere to TypeScript best practices.
- **No `any`:** The use of `any` is strictly prohibited. If a type is truly unknown, use `unknown` and perform type narrowing.
- **Backend Type Mapping:** Always refer to `openapi.json` when defining interfaces for API requests and responses. Do not invent properties that do not exist in the backend specification.
- **Explicit Return Types:** While React components can infer return types, custom hooks and utility functions should have explicit return types.

## 2. Naming Conventions
Consistency in naming helps AI agents and human developers navigate the codebase efficiently.

### Files and Directories
- **Component Files:** Use `PascalCase` and `.tsx` extension (e.g., `DocumentCard.tsx`, `Sidebar.tsx`).
- **Logic/Hook Files:** Use `camelCase` and `.ts` extension (e.g., `useDocuments.ts`, `formatDate.ts`).
- **Directories:** Use `kebab-case` or `camelCase` for feature folders (e.g., `features/auth`, `shared/components`).

### Code Elements
- **React Components:** Use `PascalCase` (e.g., `const DocumentList = () => {}`).
- **Hooks:** Use `camelCase` with the `use` prefix (e.g., `useAuth`, `useUploadDocument`).
- **Functions & Variables:** Use `camelCase` (e.g., `handleSubmit`, `isLoading`).
- **Constants:** Use `UPPER_SNAKE_CASE` (e.g., `MAX_FILE_SIZE`, `API_BASE_URL`).
- **Booleans:** Prefix with `is`, `has`, `should`, or `can` (e.g., `isOpen`, `hasError`, `shouldRetry`).

## 3. Interfaces and Types
- Prefer `interface` for declaring object shapes (especially React Props) and `type` for unions, intersections, or primitives.
- **Component Props:** Always name the interface `[ComponentName]Props`.
- **Exporting Types:** Place shared domain types in the `types/` folder of the respective feature (e.g., `features/documents/types/index.ts`).

**✅ GOOD:**
```tsx
// DocumentCard.tsx
import { Document } from '@/features/documents/types';

interface DocumentCardProps {
  document: Document;
  isExpanded?: boolean;
  onSelect: (id: string) => void;
}

export const DocumentCard = ({ document, isExpanded = false, onSelect }: DocumentCardProps) => {
  return (
    // ...
  );
};