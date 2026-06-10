import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { toast } from 'react-toastify';
import { useRegisterRequest, useRegisterComplete } from '@/features/auth/hooks/useAuth';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';

// Schema for Step 1
const emailSchema = z.object({
  email: z.string().email('Invalid email address'),
});

// Schema for Step 2
const registerSchema = z.object({
  email: z.string().email('Invalid email address'),
  username: z.string().min(3, 'Username must be at least 3 characters'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  confirmPassword: z.string().min(6, 'Please confirm your password'),
  otp: z.string().length(6, 'OTP must be exactly 6 digits'),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ['confirmPassword'],
});

type EmailFormValues = z.infer<typeof emailSchema>;
type RegisterFormValues = z.infer<typeof registerSchema>;

export const Register: React.FC = () => {
  const [step, setStep] = useState<1 | 2>(1);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const {
    register: registerEmail,
    handleSubmit: handleEmailSubmit,
    formState: { errors: emailErrors },
    getValues: getEmailValues,
  } = useForm<EmailFormValues>({
    resolver: zodResolver(emailSchema),
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
  });

  const { mutateAsync: registerRequestMutate } = useRegisterRequest();
  const { mutateAsync: registerCompleteMutate } = useRegisterComplete();

  const onEmailSubmit = async (data: EmailFormValues) => {
    setIsLoading(true);
    const toastId = toast.loading('Sending OTP...');
    try {
      await registerRequestMutate({ email: data.email });
      toast.update(toastId, { render: 'OTP sent to your email!', type: 'success', isLoading: false, autoClose: 3000 });
      setStep(2);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Failed to send OTP.';
      toast.update(toastId, { render: message, type: 'error', isLoading: false, autoClose: 3000 });
    } finally {
      setIsLoading(false);
    }
  };

  const onRegisterSubmit = async (data: RegisterFormValues) => {
    setIsLoading(true);
    const toastId = toast.loading('Creating account...');
    try {
      await registerCompleteMutate({
        email: data.email,
        username: data.username,
        password: data.password,
        otp: data.otp,
      });
      toast.update(toastId, { render: 'Registration successful! Please sign in.', type: 'success', isLoading: false, autoClose: 3000 });
      navigate('/login');
    } catch (error: any) {
      const message = error.response?.data?.message || 'Registration failed.';
      toast.update(toastId, { render: message, type: 'error', isLoading: false, autoClose: 3000 });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <h2 className="text-2xl font-semibold text-slate-100 mb-6 text-center">Create an Account</h2>
      
      {step === 1 ? (
        <form onSubmit={handleEmailSubmit(onEmailSubmit)} className="space-y-4">
          <Input
            label="Email Address"
            type="email"
            placeholder="Enter your email"
            {...registerEmail('email')}
            error={emailErrors.email?.message}
          />
          <Button type="submit" fullWidth isLoading={isLoading} className="mt-2">
            Send OTP
          </Button>
        </form>
      ) : (
        <form onSubmit={handleSubmit(onRegisterSubmit)} className="space-y-2">
          {/* Hidden email input to pass it to the final submit */}
          <input type="hidden" value={getEmailValues().email} {...register('email')} />
          
          <p className="text-sm text-slate-400 text-center mb-4 pb-2 border-b border-slate-800">
            OTP sent to <span className="font-medium text-slate-300">{getEmailValues().email}</span>
          </p>

          <Input
            label="Username"
            type="text"
            placeholder="Choose a username"
            {...register('username')}
            error={errors.username?.message}
          />

          <Input
            label="OTP Code"
            type="text"
            placeholder="Enter 6-digit OTP"
            {...register('otp')}
            error={errors.otp?.message}
          />

          <Input
            label="Password"
            type="password"
            placeholder="Create a password"
            {...register('password')}
            error={errors.password?.message}
          />

          <Input
            label="Confirm Password"
            type="password"
            placeholder="Confirm your password"
            {...register('confirmPassword')}
            error={errors.confirmPassword?.message}
          />

          <Button type="submit" fullWidth isLoading={isLoading} className="mt-4">
            Complete Registration
          </Button>
        </form>
      )}

      <div className="text-center text-sm text-slate-500 mt-6 pt-4 border-t border-slate-800">
        Already have an account?{' '}
        <Link to="/login" className="text-blue-500 hover:text-blue-400 font-medium">
          Sign in
        </Link>
      </div>
    </div>
  );
};
