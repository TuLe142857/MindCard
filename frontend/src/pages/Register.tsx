// src/pages/Register.tsx
import { useState, type FormEvent } from 'react';
import { Link } from 'react-router-dom';

export default function RegisterPage() {
  const [username, setUsername] = useState('');
  const [email,    setEmail]    = useState('');
  const [password, setPassword] = useState('');
  const [confirm,  setConfirm]  = useState('');
  const [toast,    setToast]    = useState('');

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    setToast('✨ Tính năng đăng ký sẽ sớm ra mắt!');
    setTimeout(() => setToast(''), 3000);
  };

  return (
    <div className="
      min-h-screen flex items-center justify-center
      bg-slate-950
      bg-[radial-gradient(ellipse_at_bottom_right,_var(--tw-gradient-stops))]
      from-violet-900/25 via-slate-950 to-slate-950
      px-6 py-12
    ">
      {/* Toast */}
      {toast && (
        <div className="
          fixed top-6 left-1/2 -translate-x-1/2 z-50
          px-5 py-3 rounded-xl
          bg-indigo-600 text-white text-sm font-medium
          shadow-lg shadow-indigo-500/30
          animate-bounce
        ">
          {toast}
        </div>
      )}

      <div className="w-full max-w-md">
        {/* Logo */}
        <div className="flex items-center justify-center gap-2.5 mb-8">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-indigo-500 to-violet-600 flex items-center justify-center text-white font-black text-lg shadow-lg shadow-indigo-500/30">
            M
          </div>
          <span className="text-2xl font-black bg-gradient-to-r from-indigo-400 to-violet-400 bg-clip-text text-transparent">
            MindCard
          </span>
        </div>

        {/* Card */}
        <div className="bg-slate-900/80 backdrop-blur-xl border border-slate-700/50 rounded-2xl p-8 shadow-2xl shadow-black/40">
          <h1 className="text-2xl font-bold text-white mb-1">Tạo tài khoản mới 🎉</h1>
          <p className="text-slate-400 text-sm mb-6">Bắt đầu hành trình học tập của bạn với MindCard.</p>

          <form id="register-form" onSubmit={handleSubmit} className="space-y-4">
            {/* Username */}
            <div className="space-y-1.5">
              <label htmlFor="register-username" className="block text-sm font-medium text-slate-300">
                Tên hiển thị
              </label>
              <input
                id="register-username"
                type="text"
                value={username}
                onChange={e => setUsername(e.target.value)}
                placeholder="Nguyễn Văn A"
                className="
                  w-full px-4 py-3 rounded-xl text-sm
                  bg-slate-800/70 border border-slate-700/60
                  text-slate-100 placeholder:text-slate-500
                  focus:outline-none focus:border-indigo-500/70 focus:ring-1 focus:ring-indigo-500/40
                  transition-all duration-200
                "
              />
            </div>

            {/* Email */}
            <div className="space-y-1.5">
              <label htmlFor="register-email" className="block text-sm font-medium text-slate-300">
                Email
              </label>
              <input
                id="register-email"
                type="email"
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
              <label htmlFor="register-password" className="block text-sm font-medium text-slate-300">
                Mật khẩu
              </label>
              <input
                id="register-password"
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="Tối thiểu 8 ký tự"
                className="
                  w-full px-4 py-3 rounded-xl text-sm
                  bg-slate-800/70 border border-slate-700/60
                  text-slate-100 placeholder:text-slate-500
                  focus:outline-none focus:border-indigo-500/70 focus:ring-1 focus:ring-indigo-500/40
                  transition-all duration-200
                "
              />
            </div>

            {/* Confirm password */}
            <div className="space-y-1.5">
              <label htmlFor="register-confirm" className="block text-sm font-medium text-slate-300">
                Nhập lại mật khẩu
              </label>
              <input
                id="register-confirm"
                type="password"
                value={confirm}
                onChange={e => setConfirm(e.target.value)}
                placeholder="Nhập lại mật khẩu"
                className="
                  w-full px-4 py-3 rounded-xl text-sm
                  bg-slate-800/70 border border-slate-700/60
                  text-slate-100 placeholder:text-slate-500
                  focus:outline-none focus:border-indigo-500/70 focus:ring-1 focus:ring-indigo-500/40
                  transition-all duration-200
                "
              />
            </div>

            {/* Terms */}
            <label className="flex items-start gap-2.5 text-xs text-slate-400 cursor-pointer">
              <input
                id="register-terms"
                type="checkbox"
                className="mt-0.5 rounded border-slate-600 bg-slate-800 accent-indigo-500"
              />
              <span>
                Tôi đồng ý với{' '}
                <span className="text-indigo-400 hover:underline cursor-pointer">Điều khoản sử dụng</span>
                {' '}và{' '}
                <span className="text-indigo-400 hover:underline cursor-pointer">Chính sách bảo mật</span>.
              </span>
            </label>

            {/* Submit */}
            <button
              id="register-submit"
              type="submit"
              className="
                w-full py-3 rounded-xl text-sm font-semibold text-white
                bg-gradient-to-r from-violet-600 to-indigo-600
                hover:from-violet-500 hover:to-indigo-500
                shadow-lg shadow-violet-500/25 hover:shadow-violet-500/40
                transition-all duration-200 active:scale-[0.98]
              "
            >
              Tạo tài khoản
            </button>
          </form>

          <p className="mt-5 text-center text-sm text-slate-400">
            Đã có tài khoản?{' '}
            <Link to="/login" className="font-semibold text-indigo-400 hover:text-indigo-300 transition-colors">
              Đăng nhập
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
