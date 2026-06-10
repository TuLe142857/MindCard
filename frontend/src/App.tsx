import { useEffect } from 'react';
import { RouterProvider } from 'react-router-dom';
import { router } from './router';
import { useAppDispatch, useAppSelector } from './store/hooks';
import { fetchAuth } from './store/authSlice';

function App() {
  const dispatch = useAppDispatch();
  const { isInitialized } = useAppSelector((state) => state.auth);

  useEffect(() => {
    console.log("Wellcome, rendered from App.tsx")
    dispatch(fetchAuth());
  }, [dispatch]);

  if (!isInitialized) {
    return (
      <div className="min-h-screen bg-slate-950 flex flex-col justify-center items-center">
        <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
        <p className="text-slate-400 mt-4 font-medium animate-pulse">Loading MindCard...</p>
      </div>
    );
  }

  return <RouterProvider router={router} />;
}

export default App;
