import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { toast } from 'react-toastify';
import { useResetPassword } from '@/features/auth/hooks/useAuth';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';

const resetPasswordSchema = z
  .object({
    identity: z.string().min(1, 'Email or username is required'),
    otp: z.string().length(6, 'OTP must be exactly 6 digits'),
    newPassword: z.string().min(6, 'Password must be at least 6 characters'),
    confirmPassword: z.string().min(6, 'Please confirm your password'),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Passwords don't match",
    path: ['confirmPassword'],
  });

type ResetPasswordFormValues = z.infer<typeof resetPasswordSchema>;

export const ResetPassword: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  // Get identity from ForgotPassword page state if available
  const initialIdentity = location.state?.identity || '';

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResetPasswordFormValues>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: {
      identity: initialIdentity,
    },
  });

  const { mutateAsync: resetPasswordMutate } = useResetPassword();

  const onSubmit = async (data: ResetPasswordFormValues) => {
    setIsLoading(true);
    const toastId = toast.loading('Resetting password...');
    try {
      await resetPasswordMutate({
        identity: data.identity,
        otp: data.otp,
        newPassword: data.newPassword,
      });
      toast.update(toastId, {
        render: 'Password reset successfully! Please sign in.',
        type: 'success',
        isLoading: false,
        autoClose: 3000,
      });
      navigate('/login');
    } catch (error: any) {
      const message = error.response?.data?.message || 'Failed to reset password.';
      toast.update(toastId, { render: message, type: 'error', isLoading: false, autoClose: 3000 });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <h2 className="text-2xl font-semibold text-slate-100 mb-6 text-center">
        Create New Password
      </h2>
      <p className="text-sm text-slate-400 text-center mb-6">
        Enter the OTP sent to your email and choose a new password.
      </p>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-2">
        <Input
          label="Email or Username"
          type="text"
          placeholder="Enter your email or username"
          {...register('identity')}
          error={errors.identity?.message}
        />

        <Input
          label="OTP Code"
          type="text"
          placeholder="Enter 6-digit OTP"
          {...register('otp')}
          error={errors.otp?.message}
        />

        <Input
          label="New Password"
          type="password"
          placeholder="Create new password"
          {...register('newPassword')}
          error={errors.newPassword?.message}
        />

        <Input
          label="Confirm New Password"
          type="password"
          placeholder="Confirm new password"
          {...register('confirmPassword')}
          error={errors.confirmPassword?.message}
        />

        <Button type="submit" fullWidth isLoading={isLoading} className="mt-4">
          Reset Password
        </Button>
      </form>

      <div className="text-center text-sm text-slate-500 mt-6 pt-4 border-t border-slate-800">
        <Link to="/login" className="text-blue-500 hover:text-blue-400 font-medium">
          Back to Login
        </Link>
      </div>
    </div>
  );
};
