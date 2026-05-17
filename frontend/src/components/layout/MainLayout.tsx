// src/components/layout/MainLayout.tsx
import { useState, type ReactNode } from 'react';
import Navbar from './Navbar';
import Sidebar from './Sidebar';

interface MainLayoutProps {
  children: ReactNode;
}

export default function MainLayout({ children }: MainLayoutProps) {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      {/* Navbar */}
      <Navbar onToggleSidebar={() => setSidebarOpen(prev => !prev)} />

      {/* Sidebar (overlay) */}
      <Sidebar
        isOpen={sidebarOpen}
        onClose={() => setSidebarOpen(false)}
      />

      {/* Main content */}
      <main className="max-w-screen-xl mx-auto px-4 md:px-6 py-6 space-y-10">
        {children}
      </main>
    </div>
  );
}
