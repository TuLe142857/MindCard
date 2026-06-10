import { useMutation, useQueryClient } from '@tanstack/react-query';
import { login, logout } from '../api/authApi';
import type { LoginRequest, User } from '../types';
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
