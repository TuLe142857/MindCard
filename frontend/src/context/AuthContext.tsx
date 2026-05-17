// src/context/AuthContext.tsx
import { createContext, useState, useCallback, type ReactNode } from 'react';
import { users, DEMO_PASSWORD } from '../data/mockData';
import type { User } from '../types';

interface AuthContextValue {
  currentUser: User | null;
  login: (email: string, password: string) => Promise<{ success: boolean; message?: string }>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [currentUser, setCurrentUser] = useState<User | null>(() => {
    // Persist session via sessionStorage
    const stored = sessionStorage.getItem('mindcard_user');
    if (stored) {
      try { return JSON.parse(stored) as User; } catch { return null; }
    }
    return null;
  });

  const login = useCallback(async (email: string, password: string) => {
    // Simulate async API call
    await new Promise(resolve => setTimeout(resolve, 600));

    const found = users.find(
      u => u.email.toLowerCase() === email.toLowerCase()
        && u.hashed_password === password
    );

    if (!found) {
      return { success: false, message: 'Email hoặc mật khẩu không đúng.' };
    }

    setCurrentUser(found);
    sessionStorage.setItem('mindcard_user', JSON.stringify(found));
    return { success: true };
  }, []);

  const logout = useCallback(() => {
    setCurrentUser(null);
    sessionStorage.removeItem('mindcard_user');
  }, []);

  return (
    <AuthContext.Provider value={{ currentUser, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// Suppress unused import warning for DEMO_PASSWORD (used externally)
void DEMO_PASSWORD;
