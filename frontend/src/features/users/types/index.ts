/**
 * Standard data model for a User returned from the API.
 */
export interface User {
  /** User's login name */
  username: string;
  /** User's email address */
  email: string;
  /** URL to the user's avatar image */
  avatarUrl?: string | null;
}

/**
 * Public profile of a user (does not contain sensitive info like email).
 */
export type UserPublicProfile = Omit<User, 'email'>;
