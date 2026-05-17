// src/components/ui/StarRating.tsx
interface StarRatingProps {
  rating: number;   // 0–5
  count?: number;
  size?: 'sm' | 'md';
}

export default function StarRating({ rating, count, size = 'sm' }: StarRatingProps) {
  const starSize = size === 'sm' ? 'text-sm' : 'text-base';

  return (
    <div className="flex items-center gap-1">
      <div className={`flex ${starSize}`}>
        {[1, 2, 3, 4, 5].map(star => {
          const filled = star <= Math.floor(rating);
          const half   = !filled && star === Math.ceil(rating) && rating % 1 >= 0.5;
          return (
            <span
              key={star}
              className={
                filled ? 'text-amber-400' :
                half   ? 'text-amber-300' :
                         'text-slate-600'
              }
            >
              ★
            </span>
          );
        })}
      </div>
      {rating > 0 && (
        <span className="text-xs text-slate-400">
          {rating.toFixed(1)}{count !== undefined && ` (${count})`}
        </span>
      )}
      {rating === 0 && (
        <span className="text-xs text-slate-500">Chưa có đánh giá</span>
      )}
    </div>
  );
}
