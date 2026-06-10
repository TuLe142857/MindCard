import React from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom';
import { LogOut, Search, User, Library, LayoutGrid } from 'lucide-react';
import { useAppDispatch } from '../store/hooks';
import { clearCredentials } from '../store/authSlice';
import { apiClient } from '../shared/api/apiClient';

export const MainLayout: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await apiClient.post('/api/auth/logout');
    } catch (error) {
      console.error('Logout failed:', error);
    } finally {
      dispatch(clearCredentials());
      navigate('/login');
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-200 font-sans flex flex-col">
      {/* Navbar */}
      <header className="h-16 border-b border-slate-800 bg-slate-900 flex items-center justify-between px-6 sticky top-0 z-50">
        <div className="flex items-center gap-6">
          <Link to="/explore" className="text-2xl font-bold text-blue-500">
            MindCard
          </Link>
          <nav className="hidden md:flex items-center gap-4 ml-4">
            <Link to="/explore" className="flex items-center gap-2 text-slate-300 hover:text-blue-400 transition-colors">
              <Search size={18} />
              <span>Explore</span>
            </Link>
            <Link to="/library" className="flex items-center gap-2 text-slate-300 hover:text-blue-400 transition-colors">
              <Library size={18} />
              <span>Library</span>
            </Link>
            <Link to="/my-decks" className="flex items-center gap-2 text-slate-300 hover:text-blue-400 transition-colors">
              <LayoutGrid size={18} />
              <span>My Decks</span>
            </Link>
          </nav>
        </div>

        <div className="flex items-center gap-4">
          <Link to="/profile" className="p-2 rounded-full hover:bg-slate-800 transition-colors" title="Profile">
            <User size={20} className="text-slate-300" />
          </Link>
          <button 
            onClick={handleLogout}
            className="p-2 rounded-full hover:bg-slate-800 transition-colors text-slate-300 hover:text-red-400"
            title="Logout"
          >
            <LogOut size={20} />
          </button>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-6">
        <Outlet />
      </main>
    </div>
  );
};
