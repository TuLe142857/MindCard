---
trigger: always_on
---

# Rule 03: State Management (Redux vs React Query)

## 1. The Core Philosophy: Separation of State
To avoid unnecessary re-renders, boilerplate code, and complex state synchronization, we strictly separate state into two distinct categories: **Server State** and **Client State**. 

AI Agents must clearly identify which type of state a feature requires before generating code.

## 2. Server State (Managed by React Query)
**Server State** is any data that resides on the backend and needs to be fetched, cached, synchronized, and updated.
- **Tool:** `@tanstack/react-query` (via custom hooks defined in `Rule 02`).
- **When to use:**
  - Fetching a list of items (e.g., fetching documents from the database).
  - Fetching details of a specific item.
  - Tracking loading (`isLoading`, `isPending`) and error states for API calls.
  - Mutations (creating, updating, deleting data on the server).
- **Rule:** NEVER store server data (like a list of documents) in Redux. React Query handles caching and invalidation automatically.

## 3. Client State (Managed by Redux Toolkit)
**Client State** is global data that is strictly related to the user's current session or UI state and does NOT need to be continuously synced with the backend database.
- **Tool:** `@reduxjs/toolkit` and `react-redux`.
- **Location:** Configured in `src/store/` with slices (e.g., `src/store/userSlice.ts`).
- **When to use:**
  - Storing the authenticated user's profile information (after login).
  - Storing the JWT Access Token (if not using localStorage/cookies).
  - Global UI state (e.g., `isSidebarOpen`, `theme: 'light' | 'dark'`).
  - Complex multi-step form data across different routes before submitting to the server.
- **Rule:** Keep Redux state minimal. If a piece of state is only used in one component or its immediate children, use React's built-in `useState` or `useReducer` instead.

## 4. Example: The Authentication Flow
This is how Redux and React Query work together in an Auth flow:
1. **React Query (`useMutation`):** The user submits the login form. A custom hook `useLogin` sends the credentials to the backend.
2. **Redux (`dispatch`):** Upon a successful mutation response, extract the user profile and token, then dispatch an action (e.g., `dispatch(setCredentials({ user, token }))`) to save them in the Redux store.
3. **React Query (Invalidation):** If logging out, clear the Redux store AND call `queryClient.clear()` to remove all cached sensitive server data.

## 5. Strict Guidelines for AI
- **No Redux Thunks:** Do not use `createAsyncThunk` or Redux Saga for API calls. All data fetching is the responsibility of React Query.
- **Boilerplate Reduction:** When creating a Redux slice, use modern Redux Toolkit syntax (`createSlice`). Do not write legacy Redux reducers and action creators manually.