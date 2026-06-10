---
trigger: always_on
---

# Rule 00: Tech Stack & Allowed Libraries

## 1. Core Philosophy & Strict Dependency Control
- **No Unauthorized Installations:** You are STRICTLY FORBIDDEN from adding, installing, or modifying any dependencies (e.g., running `npm install`, `yarn add`) without explicit permission from the developer.
- **Propose First:** If you believe a new third-party library is absolutely necessary to solve a problem efficiently, you must first notify the developer, explain the rationale, suggest the library, and **WAIT** for approval before proceeding.
- **No Reinventing the Wheel:** Always use the established libraries listed below for their designated purposes. Do not write custom manual implementations (e.g., custom fetch wrappers, custom CSS, manual SVG icons) if a library is already provided for that specific task.

## 2. Approved Tech Stack

### Framework & Language
- **React 18+** with **TypeScript** (Strict mode enabled).
- **Vite** as the bundler.

### Styling & UI
- **Tailwind CSS:** Must be used for ALL styling. Do not write custom `.css` or `.scss` files unless strictly necessary for global resets.
- **Lucide React (`lucide-react`):** The ONLY library used for icons. Do not use FontAwesome, Heroicons, or raw inline SVG code.

### State Management & Data Fetching
- **React Query (`@tanstack/react-query`):** MUST be used for all asynchronous server state, API fetching, caching, and mutations.
- **Redux Toolkit (`@reduxjs/toolkit`):** Used strictly for client-side global state (e.g., authenticated user session, UI theme). Do not put server data here.
- **Axios:** The designated HTTP client. All requests must route through the configured `apiClient` instance. Do not use the native `fetch` API.

### Routing
- **React Router (`react-router-dom`):** Used for all application routing and navigation.

### Forms & Validation
- **React Hook Form (`react-hook-form`):** Use this for handling all form states instead of controlled React `useState`.
- **Zod (`zod`):** Use for schema validation alongside React Hook Form.
- **Hookform Resolvers (`@hookform/resolvers`):** Use as an adapter to connect Zod schemas with React Hook Form. Typical use cases include: validating user authentication forms (login/register), creating or updating resources, and ensuring complex nested form data matches the strict Zod schema before submission.

### Utilities
- **Toast Notifications (`react-toastify`):** Use this to display success/error feedback to the user (e.g., after an API mutation). Do not build custom alert components.
- **Tailwind Merge / CLSX:** Use utility functions (usually named `cn`) to dynamically merge Tailwind classes without conflicts.