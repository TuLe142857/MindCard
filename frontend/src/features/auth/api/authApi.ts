import { apiClient } from '@/shared/api/apiClient';
import type { ApiSuccessResponse } from '@/shared/types/api';
import type {
  LoginRequest,
  User,
  RegisterOtpRequest,
  RegisterCompleteRequest,
  ForgotPasswordRequest,
  ResetPasswordRequest,
} from '../types';

/**
 * Authenticates the user and retrieves their profile.
 * Tokens are set automatically via http-only cookies by the server.
 *
 * @param credentials - The user's login credentials.
 * @returns A promise resolving to the authenticated User profile.
 */
export const login = async (credentials: LoginRequest): Promise<User> => {
  await apiClient.post('/auth/login', credentials);

  // Fetch the user profile explicitly after a successful login
  const meResponse = await apiClient.get<ApiSuccessResponse<User>>('/users/me');
  return meResponse.data.data;
};

/**
 * Logs out the current user and clears the server-side session.
 *
 * @returns A promise that resolves when logout is complete.
 */
export const logout = async (): Promise<void> => {
  await apiClient.post('/auth/logout');
};

/**
 * Requests an OTP to be sent to the provided email for registration.
 *
 * @param data - The payload containing the email address.
 * @returns A promise that resolves when the OTP request is successfully sent.
 */
export const registerRequest = async (data: RegisterOtpRequest): Promise<void> => {
  await apiClient.post('/auth/register/request', data);
};

/**
 * Completes the registration process by verifying the OTP and creating the user.
 *
 * @param data - The payload containing the email, username, password, and OTP.
 * @returns A promise that resolves when registration is successful.
 */
export const registerComplete = async (data: RegisterCompleteRequest): Promise<void> => {
  await apiClient.post('/auth/register/complete', data);
};

/**
 * Requests an OTP to be sent to the user's email for password reset.
 *
 * @param data - The payload containing the user's identity (email or username).
 * @returns A promise that resolves when the password reset OTP is successfully sent.
 */
export const forgotPassword = async (data: ForgotPasswordRequest): Promise<void> => {
  await apiClient.post('/auth/forgot_password', data);
};

/**
 * Resets the user's password using the provided OTP.
 *
 * @param data - The payload containing the user's identity, new password, and OTP.
 * @returns A promise that resolves when the password is successfully reset.
 */
export const resetPassword = async (data: ResetPasswordRequest): Promise<void> => {
  await apiClient.post('/auth/reset_password', data);
};
