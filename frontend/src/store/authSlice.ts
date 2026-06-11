import { createSlice, createAsyncThunk, type PayloadAction } from '@reduxjs/toolkit';
import { apiClient } from '@/shared/api/apiClient';
import type { User } from '@/features/users';
import type { ApiSuccessResponse } from '@/shared/types/api';

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isInitialized: boolean;
}

const initialState: AuthState = {
  user: null,
  isAuthenticated: false,
  isInitialized: false,
};

export const fetchAuth = createAsyncThunk('auth/fetchAuth', async (_, { rejectWithValue }) => {
  try {
    const response = await apiClient.get<ApiSuccessResponse<User>>('/users/me');
    return response.data.data;
  } catch {
    return rejectWithValue('Not authenticated');
  }
});

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials: (state, action: PayloadAction<{ user: User }>) => {
      state.user = action.payload.user;
      state.isAuthenticated = true;
    },
    clearCredentials: (state) => {
      state.user = null;
      state.isAuthenticated = false;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchAuth.pending, (state) => {
        state.user = null;
        state.isAuthenticated = false;
        state.isInitialized = false;
      })
      .addCase(fetchAuth.fulfilled, (state, action) => {
        state.user = action.payload;
        state.isAuthenticated = true;
        state.isInitialized = true;
      })
      .addCase(fetchAuth.rejected, (state) => {
        state.user = null;
        state.isAuthenticated = false;
        state.isInitialized = true;
      });
  },
});

export const { setCredentials, clearCredentials } = authSlice.actions;
export default authSlice.reducer;
