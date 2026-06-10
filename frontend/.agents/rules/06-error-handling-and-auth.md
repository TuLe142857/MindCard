---
trigger: always_on
---

# Rule 06: Error Handling & Authentication Flow

## 1. Standardized API Error Response
All backend error responses follow a strict structure defined by the `APIResponse` format. AI Agents must define and utilize this interface for all error handling logic:

```typescript
interface ApiErrorResponse {
  success: false;
  errorCode: string; // E.g., 'SERVER_ERROR', 'VALIDATION_ERROR'
  errorDetails: any | null; // Usually an object mapping field names to validation messages
  message: string | null;
  timestamp: number;
}
```

## 2. Error Handling Strategy (Component-First)
The Axios Interceptor should NOT swallow global errors to show generic Toasts. Errors must be passed down to the components to render appropriate Error UIs.

**Global & System Errors**
- **What:** System-wide errors such as `SERVER_ERROR` (500), `FORBIDDEN` (403), `NOT_FOUND` (404), or `RESOURCE_ALREADY_EXIST` (409).
- **How:** The Axios interceptor must simply reject the promise (`return Promise.reject(error)`).
- **UI Feedback:** Handle these at the component level using React Query's `isError` / `error` state. Render a specific custom Error UI Component (e.g., `<ErrorFallback message={error.message} />`) to allow users to see the exact issue and retry. For catastrophic unhandled errors, rely on React Error Boundaries.

**Validation / Local Errors (React Query)**
- **What:** Specifically targeted at `VALIDATION_ERROR` (422) or context-specific errors like `INVALID_OTP` (400).
- **How:** Handle these inside the component using React Query's `onError` callback.
- **UI Feedback:** Extract the `errorDetails` payload and map them directly to the corresponding form fields (e.g., using `setError` in `react-hook-form`) to display inline validation messages. Do not use Toast for form validation errors unless `errorDetails` is null.

## 3. Authentication State Management (Single Role App)
The application currently supports only ONE role (User). Guest access is STRICTLY PROHIBITED.

- **Storage:** Use Redux to store the authenticated `User` object and an `isAuthenticated` boolean in `store/authSlice.ts`.
- **Tokens:** JWT tokens (access, refresh) are managed entirely by the server via HTTP-only cookies. The frontend does NOT store them in `localStorage`.
- **Global Auth Guard:** Do not create individual `<ProtectedRoute>` wrappers for every route. Implement a global check at the root layout level. If `isAuthenticated` is false (and the current path is not `/login`, `/register`, or `/forgot-password`), force a redirect to `/login`.

## 4. Smart Token Refresh & Forced Logout
The Axios interceptor handles refreshing the token via a Request Queue. You must inspect the specific `errorCode` to determine the action.

**The Refresh Loop (Queueing)**
- **Trigger:** Only attempt to refresh the token if an API request fails with a 401 status AND the `errorCode` is exactly `"JWT_TOKEN_EXPIRED"`.
- **Action:** 
  1. Set `isRefreshing = true`.
  2. Push all subsequent API calls into a queue.
  3. Call the `/api/auth/refresh` endpoint (which automatically sends the refresh token cookie).
  4. On success: Resolve the queue to retry the original requests, and set `isRefreshing = false`.

**Immediate Forced Logout**
- **Trigger:** The interceptor MUST immediately clear the session if:
  1. The `/api/auth/refresh` API call itself fails.
  2. A 401 status is returned with `errorCode` matching `"INVALID_JWT_TOKEN"`, `"JWT_TOKEN_REVOKED"`, `"UNAUTHENTICATED"`, or `"LOGIN_FAILED"`.
- **Action:**
  1. Dispatch the Redux logout action to clear user state (`isAuthenticated = false`).
  2. Call React Query's `queryClient.clear()` to wipe all sensitive cached data.
  3. Call the `/api/auth/logout` endpoint to blacklist the token and clear the HTTP-only cookies on the server.
  4. Force a redirect to the `/login` page.