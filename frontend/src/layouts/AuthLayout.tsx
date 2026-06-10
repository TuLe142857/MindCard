import React from 'react';
import { Outlet } from 'react-router-dom';

export const AuthLayout: React.FC = () => {
  return (
    <div className="min-h-screen bg-slate-950 flex flex-col justify-center items-center p-4">
      <div className="w-full max-w-md bg-slate-900 rounded-xl shadow-xl border border-slate-800 p-8">
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold text-blue-500">MindCard</h1>
          <p className="text-slate-400 mt-2">Unlock your learning potential</p>
        </div>
        <Outlet />
      </div>
    </div>
  );
};
