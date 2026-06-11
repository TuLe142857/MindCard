import React, { useState } from 'react';
import { useAppSelector } from '@/store/hooks';
import { Camera, Save, Lock, User as UserIcon } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';

export const Profile: React.FC = () => {
  const { user } = useAppSelector((state) => state.auth);

  const [passwords, setPasswords] = useState({
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  const handleChangePassword = (e: React.FormEvent) => {
    e.preventDefault();
    console.log('Change password:', passwords);
    // API Call here
  };

  const handleAvatarChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      console.log('Upload avatar:', file);
      // API Call here
    }
  };

  return (
    <div className="max-w-4xl mx-auto flex flex-col gap-8 pb-12">
      <h1 className="text-3xl font-bold text-slate-100">Account Settings</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {/* Left Column: Avatar & Basic Info */}
        <div className="flex flex-col gap-6">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 flex flex-col items-center text-center">
            <div className="relative group mb-4">
              <div className="w-32 h-32 rounded-full overflow-hidden border-4 border-slate-800 bg-slate-950 flex items-center justify-center">
                {user?.avatarUrl ? (
                  <img src={user.avatarUrl} alt="Avatar" className="w-full h-full object-cover" />
                ) : (
                  <UserIcon size={48} className="text-slate-600" />
                )}
              </div>
              <label className="absolute inset-0 flex items-center justify-center bg-slate-950/60 rounded-full opacity-0 group-hover:opacity-100 transition-opacity cursor-pointer">
                <Camera size={24} className="text-slate-200" />
                <input
                  type="file"
                  className="hidden"
                  accept="image/*"
                  onChange={handleAvatarChange}
                />
              </label>
            </div>
            <h2 className="text-xl font-bold text-slate-100">{user?.username || 'Guest'}</h2>
            <p className="text-sm text-slate-500 mt-1">{user?.email}</p>
          </div>
        </div>

        {/* Right Column: Settings Forms */}
        <div className="md:col-span-2 flex flex-col gap-6">
          {/* Security Settings */}
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6">
            <div className="flex items-center gap-2 mb-6 border-b border-slate-800 pb-4">
              <Lock size={20} className="text-slate-400" />
              <h2 className="text-lg font-bold text-slate-200">Change Password</h2>
            </div>

            <form onSubmit={handleChangePassword} className="flex flex-col gap-4">
              <div className="flex flex-col gap-1.5">
                <label className="text-sm font-medium text-slate-400">Current Password</label>
                <input
                  type="password"
                  value={passwords.oldPassword}
                  onChange={(e) => setPasswords({ ...passwords, oldPassword: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 focus:border-blue-500 rounded-xl px-4 py-3 text-slate-100 outline-none transition-colors"
                />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="flex flex-col gap-1.5">
                  <label className="text-sm font-medium text-slate-400">New Password</label>
                  <input
                    type="password"
                    value={passwords.newPassword}
                    onChange={(e) => setPasswords({ ...passwords, newPassword: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 focus:border-blue-500 rounded-xl px-4 py-3 text-slate-100 outline-none transition-colors"
                  />
                </div>
                <div className="flex flex-col gap-1.5">
                  <label className="text-sm font-medium text-slate-400">Confirm New Password</label>
                  <input
                    type="password"
                    value={passwords.confirmPassword}
                    onChange={(e) =>
                      setPasswords({ ...passwords, confirmPassword: e.target.value })
                    }
                    className="w-full bg-slate-950 border border-slate-800 focus:border-blue-500 rounded-xl px-4 py-3 text-slate-100 outline-none transition-colors"
                  />
                </div>
              </div>
              <div className="flex justify-end mt-4">
                <Button
                  type="submit"
                  className="gap-2"
                  disabled={!passwords.oldPassword || !passwords.newPassword}
                >
                  <Save size={16} />
                  Update Password
                </Button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};
