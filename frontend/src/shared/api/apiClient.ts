import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import type { ApiErrorResponse } from '@/shared/types/api.ts';

// TODO: Ensure these paths exist when setting up Redux and React Query
// import { store } from '@/store';
// import { logout } from '@/store/authSlice';
// import { queryClient } from '@/shared/api/queryClient';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true, // Crucial for sending HTTP-only cookies
  headers: {
    'Content-Type': 'application/json',
  },
});

// State for request queueing during token refresh
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: unknown) => void;
  reject: (reason?: any) => void;
}> = [];

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

// Request Interceptor
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Note: If using HTTP-only cookies for JWT, we don't need to manually attach the token here.
    // withCredentials: true will automatically send the cookies.
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ApiErrorResponse>) => {
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
          status === 403) &&
        !originalRequest._retry;

      // Check conditions for immediate forced logout
      const isAuthError =
        status === 401 &&
        ['INVALID_JWT_TOKEN', 'JWT_TOKEN_REVOKED', 'UNAUTHENTICATED', 'LOGIN_FAILED'].includes(
          errorCode || ''
        );

      // 1. Handle Token Refresh Loop
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
          await axios.post(`${API_BASE_URL}/api/auth/refresh`, {}, { withCredentials: true });

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

      // 2. Handle Immediate Forced Logout
      if (isAuthError) {
        handleForcedLogout();
      }
    }

    return Promise.reject(error);
  }
);

/**
 * Handles clearing the user session completely and redirecting to login.
 */
const handleForcedLogout = () => {
  // Call API to blacklist token and clear cookies
  axios.post(`${API_BASE_URL}/api/auth/logout`, {}, { withCredentials: true }).catch(() => {});

  // 1. Dispatch the Redux logout action
  // if (store) store.dispatch(logout());

  // 2. Clear React Query cache to wipe sensitive data
  // if (queryClient) queryClient.clear();

  // 3. Redirect to login page
  window.location.href = '/login';
};
