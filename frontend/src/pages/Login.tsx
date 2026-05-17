// src/pages/Login.tsx
import { useState, type FormEvent } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [email,    setEmail]    = useState('');
  const [password, setPassword] = useState('');
  const [error,    setError]    = useState('');
  const [loading,  setLoading]  = useState(false);
  const [showPass, setShowPass] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!email || !password) {
      setError('Vui lòng nhập đầy đủ thông tin.');
      return;
    }
    setLoading(true);
    setError('');
    const result = await login(email, password);
    setLoading(false);
    if (result.success) {
      navigate('/');
    } else {
      setError(result.message ?? 'Đăng nhập thất bại.');
    }
  };

  return (
    <div className="
      min-h-screen flex
      bg-slate-950
      bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))]
      from-indigo-900/30 via-slate-950 to-slate-950
    ">
      {/* Left pane – decorative */}
      <div className="hidden lg:flex flex-col justify-center items-center w-1/2 p-12 relative overflow-hidden">
        {/* Background blobs */}
        <div className="absolute top-20 left-20 w-80 h-80 bg-indigo-600/20 rounded-full blur-3xl" />
        <div className="absolute bottom-32 right-10 w-64 h-64 bg-violet-600/20 rounded-full blur-3xl" />

        <div className="relative z-10 text-center space-y-6 max-w-md">
          {/* Logo */}
          <div className="flex items-center justify-center gap-3 mb-8">
            <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-indigo-500 to-violet-600 flex items-center justify-center text-white font-black text-2xl shadow-2xl shadow-indigo-500/30">
              M
            </div>
            <span className="text-3xl font-black bg-gradient-to-r from-indigo-400 to-violet-400 bg-clip-text text-transparent">
              MindCard
            </span>
          </div>

          <h2 className="text-3xl font-bold text-white leading-snug">
            Học thông minh hơn mỗi ngày
          </h2>
          <p className="text-slate-400 text-lg leading-relaxed">
            Hệ thống flashcard thông minh với thuật toán lặp lại ngắt quãng, giúp bạn ghi nhớ hiệu quả gấp đôi.
          </p>

          {/* Feature chips */}
          <div className="flex flex-wrap justify-center gap-2 mt-4">
            {['📚 Nhiều chủ đề', '🔁 Ôn tập thông minh', '⭐ Đánh giá cộng đồng', '🎯 Mục tiêu cá nhân'].map(f => (
              <span key={f} className="px-3 py-1.5 rounded-full bg-slate-800/80 border border-slate-700/60 text-sm text-slate-300">
                {f}
              </span>
            ))}
          </div>
        </div>
      </div>

      {/* Right pane – login form */}
      <div className="flex-1 flex items-center justify-center px-6 py-12">
        <div className="w-full max-w-md">
          {/* Mobile logo */}
          <div className="flex lg:hidden items-center justify-center gap-2.5 mb-8">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-500 to-violet-600 flex items-center justify-center text-white font-black text-lg">
              M
            </div>
            <span className="text-2xl font-black bg-gradient-to-r from-indigo-400 to-violet-400 bg-clip-text text-transparent">
              MindCard
            </span>
          </div>

          {/* Card */}
          <div className="bg-slate-900/80 backdrop-blur-xl border border-slate-700/50 rounded-2xl p-8 shadow-2xl shadow-black/40">
            <h1 className="text-2xl font-bold text-white mb-1">Chào mừng trở lại! 👋</h1>
            <p className="text-slate-400 text-sm mb-6">Đăng nhập để tiếp tục hành trình học tập.</p>

            {/* Hint */}
            <div className="mb-5 px-3.5 py-2.5 rounded-xl bg-indigo-500/10 border border-indigo-500/20 text-xs text-indigo-300 flex items-start gap-2">
              <span>💡</span>
              <span>
                Demo: <strong className="text-indigo-200">demo@mindcard.vn</strong> / <strong className="text-indigo-200">demo123</strong>
              </span>
            </div>

            <form id="login-form" onSubmit={handleSubmit} className="space-y-4">
              {/* Email */}
              <div className="space-y-1.5">
                <label htmlFor="login-email" className="block text-sm font-medium text-slate-300">
                  Email
                </label>
                <input
                  id="login-email"
                  type="email"
                  autoComplete="email"
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  placeholder="you@example.com"
                  className="
                    w-full px-4 py-3 rounded-xl text-sm
                    bg-slate-800/70 border border-slate-700/60
                    text-slate-100 placeholder:text-slate-500
                    focus:outline-none focus:border-indigo-500/70 focus:ring-1 focus:ring-indigo-500/40
                    transition-all duration-200
                  "
                />
              </div>

              {/* Password */}
              <div className="space-y-1.5">
                <label htmlFor="login-password" className="block text-sm font-medium text-slate-300">
                  Mật khẩu
                </label>
                <div className="relative">
                  <input
                    id="login-password"
                    type={showPass ? 'text' : 'password'}
                    autoComplete="current-password"
                    value={password}
                    onChange={e => setPassword(e.target.value)}
                    placeholder="Nhập mật khẩu"
                    className="
                      w-full px-4 py-3 pr-11 rounded-xl text-sm
                      bg-slate-800/70 border border-slate-700/60
                      text-slate-100 placeholder:text-slate-500
                      focus:outline-none focus:border-indigo-500/70 focus:ring-1 focus:ring-indigo-500/40
                      transition-all duration-200
                    "
                  />
                  <button
                    type="button"
                    onClick={() => setShowPass(p => !p)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-200 transition-colors"
                  >
                    {showPass
                      ? <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} className="w-4.5 h-4.5 w-4 h-4"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
                      : <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} className="w-4 h-4"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                    }
                  </button>
                </div>
              </div>

              {/* Error message */}
              {error && (
                <div className="px-3.5 py-2.5 rounded-xl bg-red-500/10 border border-red-500/20 text-sm text-red-400 flex items-center gap-2">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} className="w-4 h-4 shrink-0">
                    <circle cx="12" cy="12" r="10" /><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
                  </svg>
                  {error}
                </div>
              )}

              {/* Submit */}
              <button
                id="login-submit"
                type="submit"
                disabled={loading}
                className="
                  w-full py-3 rounded-xl text-sm font-semibold text-white
                  bg-gradient-to-r from-indigo-600 to-violet-600
                  hover:from-indigo-500 hover:to-violet-500
                  shadow-lg shadow-indigo-500/25 hover:shadow-indigo-500/40
                  transition-all duration-200 active:scale-[0.98]
                  disabled:opacity-60 disabled:cursor-not-allowed
                  flex items-center justify-center gap-2
                "
              >
                {loading ? (
                  <>
                    <svg className="w-4 h-4 animate-spin" viewBox="0 0 24 24" fill="none">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="white" strokeWidth="3" />
                      <path className="opacity-75" fill="white" d="M4 12a8 8 0 018-8v4l3-3-3-3V0a12 12 0 100 24v-4l-3 3 3 3v-4a8 8 0 01-8-8z"/>
                    </svg>
                    Đang đăng nhập...
                  </>
                ) : 'Đăng nhập'}
              </button>
            </form>

            <p className="mt-5 text-center text-sm text-slate-400">
              Chưa có tài khoản?{' '}
              <Link to="/register" className="font-semibold text-indigo-400 hover:text-indigo-300 transition-colors">
                Đăng ký ngay
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}