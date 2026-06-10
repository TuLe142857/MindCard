import React from 'react';
import { Link } from 'react-router-dom';

export const ForgotPassword: React.FC = () => {
  return (
    <div>
      <h2 className="text-2xl font-semibold text-slate-100 mb-6 text-center">Reset Password</h2>
      <div className="text-slate-400 text-center mb-6">
        Forgot password form goes here
      </div>
      <div className="text-center text-sm text-slate-500 mt-6">
        Remembered? <Link to="/login" className="text-blue-500 hover:text-blue-400">Back to Login</Link>
      </div>
    </div>
  );
};
