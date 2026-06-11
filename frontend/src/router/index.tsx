import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AuthLayout } from '../layouts/AuthLayout';
import { MainLayout } from '../layouts/MainLayout';
import { ProtectedRoute } from './ProtectedRoute';
import { PublicRoute } from './PublicRoute';

// Auth Pages
import { Login } from '@/pages/auth/Login';
import { Register } from '@/pages/auth/Register';
import { ForgotPassword } from '@/pages/auth/ForgotPassword';
import { ResetPassword } from '@/pages/auth/ResetPassword';

// Main Pages
import { Explore } from '@/pages/main/Explore';
import { Library } from '@/pages/main/Library';
import { MyDecks } from '@/pages/main/MyDecks';
import { Profile } from '@/pages/main/Profile';
import { PublicProfile } from '@/pages/main/PublicProfile';
import { DeckDetails } from '@/pages/main/DeckDetails';
import { StudySession } from '@/pages/main/StudySession';
import { SyncDeck } from '@/pages/main/SyncDeck';

// Generic
import { NotFound } from '../pages/NotFound';

export const router = createBrowserRouter([
  {
    // Public routes (only accessible when NOT logged in)
    element: <PublicRoute />,
    children: [
      {
        element: <AuthLayout />,
        children: [
          { path: '/login', element: <Login /> },
          { path: '/register', element: <Register /> },
          { path: '/forgot-password', element: <ForgotPassword /> },
          { path: '/reset-password', element: <ResetPassword /> },
        ],
      },
    ],
  },
  {
    // Protected routes (only accessible when logged in)
    element: <ProtectedRoute />,
    children: [
      {
        element: <MainLayout />,
        children: [
          // If accessing root "/", redirect to explore
          { path: '/', element: <Navigate to="/explore" replace /> },
          { path: '/explore', element: <Explore /> },
          { path: '/library', element: <Library /> },
          { path: '/my-decks', element: <MyDecks /> },
          { path: '/profile', element: <Profile /> },
          { path: '/user/:username', element: <PublicProfile /> },
          { path: '/deck/:deckId', element: <DeckDetails /> },
          { path: '/sync/:savedDeckId', element: <SyncDeck /> },
        ],
      },
      // Focus mode routes (no MainLayout)
      { path: '/study/:savedDeckId', element: <div className="min-h-screen bg-slate-950 p-4 md:p-8 text-slate-200"><StudySession /></div> },
    ],
  },
  {
    // Catch-all 404 route
    path: '*',
    element: <NotFound />,
  },
]);
