---
trigger: always_on
---

# Rule 04: Components, Pages & UI Conventions

## 1. Pages vs. Components (Smart vs. Dumb)
We strictly separate the responsibility of UI elements into **Pages** (Smart) and **Components** (Dumb/Presentational).

### Pages (`src/pages/`)
- **Role:** Route entry points and data orchestrators.
- **Responsibilities:** 
  - Call custom hooks (React Query) to fetch data.
  - Access global state (Redux).
  - Handle routing logic (navigate, read URL params).
  - Pass data and callbacks down to Presentational Components via props.
- **Rule:** Do not write complex UI layouts directly inside a Page. Compose it using imported components.

### Components (`src/features/*/components/` or `src/shared/components/`)
- **Role:** Presentational UI.
- **Responsibilities:**
  - Receive data via `props` and render the UI.
  - Emit events back to the parent via callback props (e.g., `onClick`, `onSubmit`).
- **Rule:** Components should generally not fetch their own data via API or read deeply from global Redux state unless absolutely necessary for performance.

## 2. Layouts (`src/layouts/`)
Use Layout components to wrap pages that share the same structural shell (e.g., Sidebars, Headers, Footers).
- Example: `MainLayout.tsx` for the dashboard, `AuthLayout.tsx` for login/register pages.
- Use the `children` prop or `react-router-dom`'s `<Outlet />` to render the nested page content.

## 3. Styling Rules (Tailwind CSS)
All styling must be done using **Tailwind CSS** utility classes. No custom CSS files unless strictly required for global resets or third-party library overrides.

- **Class Ordering:** Keep class names organized. (Tip: Use a formatter or standard ordering: Layout/Display -> Spacing/Sizing -> Typography -> Colors -> Effects).
- **Dynamic Classes:** Always use a utility function (commonly named `cn`, combining `clsx` and `tailwind-merge`) to handle conditional classes and avoid Tailwind conflicts.

**✅ GOOD:**
```tsx
import { cn } from '@/shared/utils/cn';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary';
}

export const Button = ({ variant = 'primary', className, ...props }: ButtonProps) => {
  return (
    <button 
      className={cn(
        "px-4 py-2 rounded-md font-medium transition-colors",
        variant === 'primary' ? "bg-blue-600 text-white hover:bg-blue-700" : "bg-gray-200 text-gray-800 hover:bg-gray-300",
        className // Allows parent to pass custom overrides safely
      )}
      {...props}
    />
  );
};

## 4. UI Design & Theme Guidelines
To maintain a consistent and premium user experience, adhere to the following design conventions across all UI elements:

- **Color Palette (Dark Theme Focus):**
  - **Backgrounds:** Use `slate` as the primary neutral. Typical backgrounds use `bg-slate-950` for the app body, `bg-slate-900` for cards/panels, and `bg-slate-800` for borders/dividers.
  - **Text:** Use `text-slate-100` or `200` for primary headings, `text-slate-400` or `500` for secondary text/descriptions.
  - **Accents:** Use `blue-500` or `blue-600` as the primary brand/action color.
  - **Semantic Colors:** `green-400/500` for success or "Answer" side of cards, `red-400/500` for error states/delete actions, `yellow-500` for ratings.

- **Glassmorphism & Premium Depth:**
  - Utilize translucent backgrounds with blurs for overlays/modals (e.g., `bg-slate-950/80 backdrop-blur-sm`).
  - Create glowing effects for primary buttons or decorative elements using shadows (`shadow-[0_0_15px_rgba(37,99,235,0.3)]`) or absolute blurred circles.
  - Always use subtle borders to define shapes in dark mode (`border border-slate-800`).

- **Interactions & Animations:**
  - Elements should feel responsive. Use `transition-colors`, `hover:bg-slate-800`, or hover ring effects on interactive items.
  - Use simple intro animations for Modals and Cards (e.g., `animate-in fade-in zoom-in-95`).

- **Icons:** Use **Lucide React** (`lucide-react`) for all UI icons. Keep stroke width and sizes consistent (typically `size={16}` or `size={20}`).