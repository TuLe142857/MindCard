import { useMutation, useQueryClient } from '@tanstack/react-query';
import {
  login,
  logout,
  registerRequest,
  registerComplete,
  forgotPassword,
  resetPassword,
} from '../api/authApi';
import type { LoginRequest } from '../types';
import type { User } from '@/features/users';
import { useDispatch } from 'react-redux';
import { setCredentials, clearCredentials } from '@/store/authSlice';
import { useNavigate } from 'react-router-dom';

/**
 * Hook to handle user login.
 * Updates the Redux store with user data and redirects to the home page upon success.
 *
 * @returns React Query mutation object for login.
 */
export const useLogin = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (credentials: LoginRequest) => login(credentials),
    onSuccess: (user: User) => {
      dispatch(setCredentials({ user }));
      navigate('/');
    },
  });
};

/**
 * Hook to handle user logout.
 * Clears Redux state, React Query cache, and redirects to the login page.
 *
 * @returns React Query mutation object for logout.
 */
export const useLogout = () => {
  const dispatch = useDispatch();
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
      // Even if API logout fails, clear local session for security
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
    mutationFn: registerRequest,
  });
};

/**
 * Hook to handle completing the user registration.
 *
 * @returns React Query mutation object for the complete registration request.
 */
export const useRegisterComplete = () => {
  return useMutation({
    mutationFn: registerComplete,
  });
};

/**
 * Hook to handle requesting a password reset OTP.
 *
 * @returns React Query mutation object for the forgot password request.
 */
export const useForgotPassword = () => {
  return useMutation({
    mutationFn: forgotPassword,
  });
};

/**
 * Hook to handle resetting the user password.
 *
 * @returns React Query mutation object for the reset password request.
 */
export const useResetPassword = () => {
  return useMutation({
    mutationFn: resetPassword,
  });
};
