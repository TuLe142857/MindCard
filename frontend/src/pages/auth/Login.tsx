import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { toast } from 'react-toastify';
import { useAppDispatch } from '@/store/hooks';
import { fetchAuth } from '@/store/authSlice';
import { useLogin } from '@/features/auth/hooks/useAuth';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';

const loginSchema = z.object({
  identity: z.string().min(1, 'Username or email is required'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginFormValues = z.infer<typeof loginSchema>;

export const Login: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useAppDispatch();

  const from = location.state?.from?.pathname || '/explore';

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
  });

  const { mutateAsync: loginMutate } = useLogin();

  const onSubmit = async (data: LoginFormValues) => {
    setIsLoading(true);
    const toastId = toast.loading('Signing in...');
    try {
      await loginMutate(data);
      await dispatch(fetchAuth()).unwrap();
      toast.update(toastId, {
        render: 'Welcome back!',
        type: 'success',
        isLoading: false,
        autoClose: 3000,
      });
      navigate(from, { replace: true });
    } catch (error: any) {
      const message =
        error.response?.data?.message || 'Login failed. Please check your credentials.';
      toast.update(toastId, { render: message, type: 'error', isLoading: false, autoClose: 3000 });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <h2 className="text-2xl font-semibold text-slate-100 mb-6 text-center">Sign In</h2>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-2">
        <Input
          label="Email or Username"
          type="text"
          placeholder="Enter your email or username"
          {...register('identity')}
          error={errors.identity?.message}
        />

        <div>
          <Input
            label="Password"
            type="password"
            placeholder="Enter your password"
            {...register('password')}
            error={errors.password?.message}
          />
          <div className="flex justify-end mt-1">
            <Link
              to="/forgot-password"
              className="text-sm text-blue-500 hover:text-blue-400 transition-colors"
            >
              Forgot password?
            </Link>
          </div>
        </div>

        <Button type="submit" fullWidth isLoading={isLoading} className="mt-4">
          Sign In
        </Button>
      </form>

      <div className="text-center text-sm text-slate-500 mt-6 pt-4 border-t border-slate-800">
        Don't have an account?{' '}
        <Link to="/register" className="text-blue-500 hover:text-blue-400 font-medium">
          Register
        </Link>
      </div>
    </div>
  );
};
