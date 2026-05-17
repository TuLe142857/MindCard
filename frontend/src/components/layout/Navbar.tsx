// src/components/layout/Navbar.tsx
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

interface NavbarProps {
  onToggleSidebar: () => void;
}

export default function Navbar({ onToggleSidebar }: NavbarProps) {
  const { currentUser } = useAuth();
  const navigate = useNavigate();

  return (
    <header className="
      sticky top-0 z-40
      flex items-center gap-4 h-16 px-4 md:px-6
      bg-slate-900/80 backdrop-blur-xl
      border-b border-slate-700/50
      shadow-sm shadow-slate-900/40
    ">
      {/* Logo */}
      <button
        id="navbar-logo"
        onClick={() => navigate('/')}
        className="flex items-center gap-2.5 shrink-0 group"
      >
        <div className="
          w-8 h-8 rounded-xl
          bg-gradient-to-br from-indigo-500 to-violet-600
          flex items-center justify-center text-white font-bold text-sm
          shadow-lg shadow-indigo-500/30
          group-hover:shadow-indigo-500/50 transition-shadow
        ">
          M
        </div>
        <span className="
          hidden sm:block
          text-lg font-bold
          bg-gradient-to-r from-indigo-400 to-violet-400 bg-clip-text text-transparent
        ">
          MindCard
        </span>
      </button>

      {/* Search bar */}
      <div className="flex-1 max-w-xl mx-auto">
        <label htmlFor="navbar-search" className="sr-only">Tìm kiếm</label>
        <div className="relative">
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth={2}
            className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none"
          >
            <circle cx="11" cy="11" r="8" />
            <line x1="21" y1="21" x2="16.65" y2="16.65" />
          </svg>
          <input
            id="navbar-search"
            type="text"
            placeholder="Tìm kiếm bộ thẻ, chủ đề..."
            className="
              w-full pl-10 pr-4 py-2 text-sm
              bg-slate-800/70 border border-slate-700/60
              rounded-xl text-slate-200 placeholder:text-slate-500
              focus:outline-none focus:border-indigo-500/70 focus:ring-1 focus:ring-indigo-500/40
              transition-all duration-200
            "
          />
        </div>
      </div>

      {/* Right side: user avatar + hamburger */}
      <div className="flex items-center gap-2 shrink-0">
        {/* User avatar */}
        {currentUser && (
          <div className="hidden sm:flex items-center gap-2.5">
            <span className="text-sm text-slate-400">
              {currentUser.username.split(' ').slice(-1)[0]}
            </span>
            <img
              src={currentUser.avatar}
              alt={currentUser.username}
              className="w-8 h-8 rounded-full border-2 border-indigo-500/50 bg-slate-700"
            />
          </div>
        )}

        {/* Hamburger button */}
        <button
          id="navbar-hamburger"
          onClick={onToggleSidebar}
          title="Mở menu"
          className="
            p-2 rounded-xl text-slate-400
            hover:text-slate-200 hover:bg-slate-700/60
            transition-all duration-200 active:scale-90
          "
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} className="w-5 h-5">
            <line x1="3" y1="6"  x2="21" y2="6" />
            <line x1="3" y1="12" x2="21" y2="12" />
            <line x1="3" y1="18" x2="21" y2="18" />
          </svg>
        </button>
      </div>
    </header>
  );
}
