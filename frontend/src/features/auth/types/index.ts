/**
 * Standard data model for a User returned from the API.
 */
export interface User {
  /** Unique identifier for the user */
  id: number;
  /** User's login name */
  username: string;
  /** User's email address */
  email: string;
  /** URL to the user's avatar image */
  avatarUrl?: string | null;
}

/**
 * Payload required for the login API request.
 */
export interface LoginRequest {
  /** Username or email used for authentication */
  identity?: string;
  /** User's password */
  password: string;
}

/**
 * Payload required to request an OTP for new account registration.
 */
export interface RegisterOtpRequest {
  /** Email address to receive the registration OTP */
  email: string;
}

/**
 * Payload required to verify OTP and complete the account registration process.
 */
export interface RegisterCompleteRequest {
  /** User's email address */
  email: string;
  /** Unique username for the new account */
  username: string;
  /** Raw password for the new account */
  password: string;
  /** The 6-digit OTP code sent to the email */
  otp: string;
}

/**
 * Payload required to request a password reset OTP.
 */
export interface ForgotPasswordRequest {
  /** Email or username of the user requesting password reset */
  identity: string;
}

/**
 * Payload required to verify OTP and set a new password.
 */
export interface ResetPasswordRequest {
  /** Email or username of the account to reset */
  identity: string;
  /** The new raw password */
  newPassword: string;
  /** The 6-digit OTP code received from email */
  otp: string;
}
