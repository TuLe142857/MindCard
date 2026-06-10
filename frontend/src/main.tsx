import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { Provider } from 'react-redux';
import { store } from './store';
import { clearCredentials } from './store/authSlice';
import { registerAfterForcedLogoutCallback } from './shared/api/apiClient';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './index.css';
import App from './App.tsx';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false, // often a good default
      retry: 1, // default is 3
    },
  },
});

// Callback Registry: Define behavior to clear store and cache after a forced 401 unauthorized logout.
// The API call to logout is already handled by apiClient.
registerAfterForcedLogoutCallback(() => {
  store.dispatch(clearCredentials());
  queryClient.clear();
});

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Provider store={store}>
      <QueryClientProvider client={queryClient}>
        <App />
        <ToastContainer
          position="top-right"
          autoClose={3000}
          hideProgressBar={false}
          aria-label="Notification Container"
        />
        <ReactQueryDevtools initialIsOpen={false} />
      </QueryClientProvider>
    </Provider>
  </StrictMode>
);
