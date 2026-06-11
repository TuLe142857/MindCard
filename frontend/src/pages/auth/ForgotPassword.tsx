import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { toast } from 'react-toastify';
import { useForgotPassword } from '@/features/auth/hooks/useAuth';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';

const forgotPasswordSchema = z.object({
  identity: z.string().min(1, 'Email or username is required'),
});

type ForgotPasswordFormValues = z.infer<typeof forgotPasswordSchema>;

export const ForgotPassword: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ForgotPasswordFormValues>({
    resolver: zodResolver(forgotPasswordSchema),
  });

  const { mutateAsync: forgotPasswordMutate } = useForgotPassword();

  const onSubmit = async (data: ForgotPasswordFormValues) => {
    setIsLoading(true);
    const toastId = toast.loading('Sending OTP...');
    try {
      await forgotPasswordMutate(data);
      toast.update(toastId, {
        render: 'OTP sent to your email!',
        type: 'success',
        isLoading: false,
        autoClose: 3000,
      });
      // Navigate to reset password and pass the identity so the user doesn't have to type it again
      navigate('/reset-password', { state: { identity: data.identity } });
    } catch (error: unknown) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const message = (error as any)?.response?.data?.message || 'Failed to send OTP.';
      toast.update(toastId, { render: message, type: 'error', isLoading: false, autoClose: 3000 });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <h2 className="text-2xl font-semibold text-slate-100 mb-6 text-center">Reset Password</h2>
      <p className="text-sm text-slate-400 text-center mb-6">
        Enter your email or username and we'll send you an OTP to reset your password.
      </p>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          label="Email or Username"
          type="text"
          placeholder="Enter your email or username"
          {...register('identity')}
          error={errors.identity?.message}
        />
        <Button type="submit" fullWidth isLoading={isLoading} className="mt-2">
          Send OTP
        </Button>
      </form>

      <div className="text-center text-sm text-slate-500 mt-6 pt-4 border-t border-slate-800">
        Remembered your password?{' '}
        <Link to="/login" className="text-blue-500 hover:text-blue-400 font-medium">
          Back to Login
        </Link>
      </div>
    </div>
  );
};
