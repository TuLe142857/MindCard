import React from 'react';

export const Profile: React.FC = () => {
  return (
    <div>
      <h1 className="text-3xl font-bold text-slate-100 mb-6">My Profile</h1>
      <div className="p-8 bg-slate-900 border border-slate-800 rounded-xl text-center text-slate-400">
        Profile info and settings go here
      </div>
    </div>
  );
};
