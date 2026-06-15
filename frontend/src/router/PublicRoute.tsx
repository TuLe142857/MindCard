import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAppSelector } from '../store/hooks';

export const PublicRoute: React.FC = () => {
  const { isAuthenticated } = useAppSelector((state) => state.auth);

  // Redirect authenticated users away from auth pages to /explore
  if (isAuthenticated) {
    return <Navigate to="/explore" replace />;
  }

  return <Outlet />;
};
