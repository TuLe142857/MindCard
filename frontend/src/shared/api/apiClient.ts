import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import type { ApiErrorResponse } from '@/shared/types/api.ts';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// Callback Registry: Allows the main app to define what happens on forced logout (e.g. clearing store/cache)
// This avoids importing store/queryClient directly and prevents circular dependencies.
let onForcedLogoutCallback: (() => void) | null = null;

/**
 * Registers a callback to be executed AFTER a forced logout (e.g., due to 401 Unauthorized).
 * The API call to logout and clear cookies is already handled internally by the apiClient.
 *
 * Only use this callback to clear client-side state (like Redux or React Query cache).
 *
 * @param callback - The cleanup function to execute after logout.
 */
export const registerAfterForcedLogoutCallback = (callback: () => void) => {
  onForcedLogoutCallback = callback;
};

/**
 * Handles clearing the user session completely and redirecting to login.
 */
const handleForcedLogout = () => {
  // Call API to blacklist token and clear cookies
  axios.post(`${API_BASE_URL}/auth/logout`, {}, { withCredentials: true }).catch(() => {});

  // Execute external state cleanup (Redux & React Query) via the registered callback
  // This will automatically trigger ProtectedRoute to redirect to /login
  if (onForcedLogoutCallback) {
    onForcedLogoutCallback();
  }
};

export const apiClient = axios.create({
  baseURL: `${API_BASE_URL}`,
  withCredentials: true, // Crucial for sending HTTP-only cookies
  headers: {
    'Content-Type': 'application/json',
  },
});

// State for request queueing during token refresh
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: unknown) => void;
  reject: (reason?: AxiosError<ApiErrorResponse>) => void;
}> = [];

const processQueue = (error: AxiosError<ApiErrorResponse> | null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(undefined);
    }
  });
  failedQueue = [];
};

const debug_api_success = (res) => {
  console.group(`Debug API: ${res.config?.method?.toUpperCase()} ${res.config.url}`);
  console.log('Request config:', res.config);
  console.log('Response data:', res.data);
  console.groupEnd();
};

const debug_api_error = (err) => {
  console.group(`Debug API ERROR: ${err.config?.method?.toUpperCase()} ${err.config.url}`);
  console.log('Request:', err.config);
  console.log('Error response:', err.response?.data);
  console.log('Error message:', err.message);
  console.groupEnd();
};

// Response Interceptor
apiClient.interceptors.response.use(
  (response) => {
    if (import.meta.env.DEV) {
      debug_api_success(response);
    }
    return response;
  },
  async (error: AxiosError<ApiErrorResponse>) => {
    if (import.meta.env.DEV) {
      debug_api_error(error);
    }
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    if (error.response) {
      const status = error.response.status;
      const data = error.response.data;
      const errorCode = data?.errorCode;

      // Check conditions for call refresh api
      const needRefresh =
        (errorCode === 'JWT_TOKEN_EXPIRED' ||
          errorCode === 'INVALID_JWT_TOKEN' ||
          errorCode === 'JWT_TOKEN_REVOKED' ||
          status === 403 ||
          status === 401) &&
        !originalRequest._retry;

      if (needRefresh && !originalRequest._retry) {
        if (isRefreshing) {
          // If already refreshing, queue the request
          return new Promise(function (resolve, reject) {
            failedQueue.push({ resolve, reject });
          })
            .then(() => {
              return apiClient(originalRequest);
            })
            .catch((err) => {
              return Promise.reject(err);
            });
        }

        originalRequest._retry = true;
        isRefreshing = true;

        try {
          // Attempt to refresh the token
          await axios.post(`${API_BASE_URL}/auth/refresh`, {}, { withCredentials: true });

          isRefreshing = false;
          processQueue(null);

          // Retry the original request
          return apiClient(originalRequest);
        } catch (refreshError) {
          isRefreshing = false;
          processQueue(refreshError);

          // If refresh fails, force logout
          handleForcedLogout();

          return Promise.reject(refreshError);
        }
      }
    }

    return Promise.reject(error);
  }
);
