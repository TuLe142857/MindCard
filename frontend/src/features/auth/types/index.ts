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
