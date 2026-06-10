import React from 'react';
import { Link } from 'react-router-dom';

export const Register: React.FC = () => {
  return (
    <div>
      <h2 className="text-2xl font-semibold text-slate-100 mb-6 text-center">Create an Account</h2>
      <div className="text-slate-400 text-center mb-6">
        Register form goes here (Phase 2)
      </div>
      <div className="text-center text-sm text-slate-500 mt-6">
        Already have an account? <Link to="/login" className="text-blue-500 hover:text-blue-400">Sign in</Link>
      </div>
    </div>
  );
};
