---
trigger: always_on
---
# Rule 07: Documentation & TSDoc Conventions

## 1. Core Philosophy: Self-Documenting Code First
- **Self-Explanatory Code:** Clear variable names, function names, and TypeScript syntax are always the top priority. Only write comments when the code cannot explain the "WHY", or to detail the "WHAT" for public/shared APIs.
- **TSDoc Standards:** You MUST use the `/** ... */` (TSDoc/JSDoc) format for all shared components, hooks, utilities, and essential interfaces so that IDEs can automatically display tooltips. DO NOT use `//` for documentation blocks.

## 2. React Components & Props
All components placed in `src/shared/components/` or shared across multiple features must be clearly documented.

- **Props Interface:** Document each important prop, especially optional props or those with specific behaviors.
- **Component:** Write a brief description of the component's purpose. The `@example` tag is **optional** and should only be used when the component's logic or usage is complex and important.

**GOOD:**
```tsx
/**
 * Configuration properties for the document display card.
 */
interface DocumentCardProps {
  /** The document data to display */
  document: Document;
  /**
   * The expanded state of the card.
   * @default false
   */
  isExpanded?: boolean;
  /** Callback triggered when the user selects the document */
  onSelect: (id: string) => void;
}

/**
 * Displays a summary of a document in the Document Hub.
 * Supports collapsed and expanded states to view details.
 *
 * @example
 * ```tsx
 * <DocumentCard
 * document={docData}
 * isExpanded={true}
 * onSelect={(id) => handleSelect(id)}
 * />
 * ```
 */
export const DocumentCard = ({ document, isExpanded = false, onSelect }: DocumentCardProps) => {
  // ...
};
```
## 3. Custom Hooks (React Query & Logic)
Custom hooks handling complex logic, especially data fetching/mutations, must clearly describe input parameters and return types.

Must use `@param` and `@returns`.

**GOOD:**
```typescript
/**
 * Hook to handle the logic for uploading a new document to the server.
 * Automatically invalidates the document list query cache upon successful upload.
 *
 * @param categoryId - The ID of the category where the document will be saved.
 * @returns An object containing the `uploadDoc` mutation function and React Query loading/error states.
 */
export const useUploadDocument = (categoryId: string) => {
  // ... implementation
};
```

## 4. Types & Interfaces (Domain Models)
Interfaces representing Data Models from the backend (as defined in openapi.json) must have comments explaining the fields, especially constants, statuses, or measurement units.

**GOOD:**
```typescript
/**
 * Standard data model for a Document returned from the API.
 */
export interface Document {
  /** Unique UUID identifier of the document */
  id: string;
  /** Full filename including the extension (e.g., report.pdf) */
  filename: string;
  /**
   * File size in bytes.
   * Needs to be formatted to MB or KB when displayed on the UI.
   */
  sizeInBytes: number;
  /**
   * Moderation status of the document.
   * - `PENDING`: Waiting for approval
   * - `APPROVED`: Publicly visible
   * - `REJECTED`: Rejected
   */
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
}
```

## 5. Inline Comments
Minimize Usage: Do not explain what the code is doing (TypeScript/syntax should handle that).

Use Only When: You need to explain why a specific workaround was chosen, explain a complex Regex expression, or justify the use of `@ts-ignore` (which must always be accompanied by a comment).

**BAD:**
```typescript
// Fetch user list
const users = await fetchUsers();
// Increment counter by 1
counter += 1;
```

**GOOD:**
```typescript
// Timeout is set to 5000ms because the thumbnail generation API takes longer than usual
const timeout = 5000;

// eslint-disable-next-line react-hooks/exhaustive-deps
// Ignoring the `userToken` dependency here to prevent an infinite re-render loop due to legacy API design
```