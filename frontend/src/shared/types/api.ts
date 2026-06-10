/**
 * Base APIResponse
 */
interface BaseResponse {
  success: boolean;
  message?: string | null;

  /** Timestamp in milliseconds*/
  timestamp: number;
}

export type ErrorCode =
  | 'ACTION_ALREADY_PERFORMED'
  | 'INVALID_OTP'
  | 'INVALID_JWT_TOKEN'
  | 'JWT_TOKEN_EXPIRED'
  | 'JWT_TOKEN_REVOKED'
  | 'LOGIN_FAILED'
  | 'UNAUTHENTICATED'
  | 'FORBIDDEN'
  | 'NOT_FOUND'
  | 'RESOURCE_NOT_FOUND'
  | 'USER_NOT_FOUND'
  | 'RESOURCE_ALREADY_EXIST'
  | 'VALIDATION_ERROR'
  | 'FILE_UPLOAD_FAILED'
  | 'SERVER_ERROR';

export interface ApiErrorResponse extends BaseResponse {
  success: false;
  errorCode: ErrorCode;

  /** Map<fields, error details> */
  errorDetails?: Record<string, string>;
}

export interface ApiSuccessResponse<T> extends BaseResponse {
  success: true;
  data: T;
}

export interface ApiPaginatedResponse<T> extends BaseResponse {
  data: T[];
  meta: {
    currentPage: number;
    pageSize: number;
    totalItems: number;
    totalPages: number;
    hasNext: boolean;
    hasPrev: boolean;
  };
}

export interface ApiPaginatedQuery {
  page?: number;
  limit?: number;
  sortBy?: string[];
}
