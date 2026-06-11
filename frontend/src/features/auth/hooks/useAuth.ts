import { useMutation, useQueryClient } from '@tanstack/react-query';
import {
  login,
  logout,
  registerRequest,
  registerComplete,
  forgotPassword,
  resetPassword,
} from '../api/authApi';
import type {
  LoginRequest,
  RegisterOtpRequest,
  RegisterCompleteRequest,
  ForgotPasswordRequest,
  ResetPasswordRequest,
} from '../types';
import { useAppDispatch } from '@/store/hooks';
import { clearCredentials } from '@/store/authSlice';
import { useNavigate } from 'react-router-dom';

/**
 * Hook to handle user login.
 *
 * @returns React Query mutation object for login.
 */
export const useLogin = () => {
  return useMutation({
    mutationFn: (credentials: LoginRequest) => login(credentials),
  });
};

/**
 * Hook to handle user logout.
 * Clears Redux state, React Query cache, and redirects to the login page.
 *
 * @returns React Query mutation object for logout.
 */
export const useLogout = () => {
  const dispatch = useAppDispatch();
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: logout,
    onSuccess: () => {
      dispatch(clearCredentials());
      queryClient.clear();
      navigate('/login');
    },
    onError: () => {
      dispatch(clearCredentials());
      queryClient.clear();
      navigate('/login');
    },
  });
};

/**
 * Hook to handle requesting a registration OTP.
 *
 * @returns React Query mutation object for the register request.
 */
export const useRegisterRequest = () => {
  return useMutation({
    mutationFn: (data: RegisterOtpRequest) => registerRequest(data),
  });
};

/**
 * Hook to handle completing the user registration.
 *
 * @returns React Query mutation object for the complete registration request.
 */
export const useRegisterComplete = () => {
  return useMutation({
    mutationFn: (data: RegisterCompleteRequest) => registerComplete(data),
  });
};

/**
 * Hook to handle requesting a password reset OTP.
 *
 * @returns React Query mutation object for the forgot password request.
 */
export const useForgotPassword = () => {
  return useMutation({
    mutationFn: (data: ForgotPasswordRequest) => forgotPassword(data),
  });
};

/**
 * Hook to handle resetting the user password.
 *
 * @returns React Query mutation object for the reset password request.
 */
export const useResetPassword = () => {
  return useMutation({
    mutationFn: (data: ResetPasswordRequest) => resetPassword(data),
  });
};
