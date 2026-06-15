import React from 'react';
import { ChevronLeft, ChevronRight, MoreHorizontal } from 'lucide-react';
import { cn } from '@/shared/utils/cn';
import { Button } from './Button';

export interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onNextPage?: () => void;
  onPreviousPage?: () => void;
  onSetPage?: (page: number) => void;
  className?: string;
  siblingCount?: number;
}

export const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  onNextPage,
  onPreviousPage,
  onSetPage,
  className,
  siblingCount = 1,
}) => {
  // If there's only 1 page (or less), don't render pagination
  if (totalPages <= 1) return null;

  const generatePagination = () => {
    // Total numbers to show in pagination (excluding prev/next)
    // 1st page + last page + current page + 2 * siblingCount + 2 * ellipsis
    const totalPageNumbers = siblingCount + 5;

    // If total pages is small enough, simply show all without ellipsis
    if (totalPageNumbers >= totalPages) {
      return Array.from({ length: totalPages }, (_, i) => i + 1);
    }

    const leftSiblingIndex = Math.max(currentPage - siblingCount, 1);
    const rightSiblingIndex = Math.min(currentPage + siblingCount, totalPages);

    const showLeftEllipsis = leftSiblingIndex > 2;
    const showRightEllipsis = rightSiblingIndex < totalPages - 1;

    const firstPageIndex = 1;
    const lastPageIndex = totalPages;

    // Case 1: No left ellipsis, show right ellipsis
    if (!showLeftEllipsis && showRightEllipsis) {
      const leftItemCount = 3 + 2 * siblingCount;
      const leftRange = Array.from({ length: leftItemCount }, (_, i) => i + 1);
      return [...leftRange, '...', lastPageIndex];
    }

    // Case 2: Show left ellipsis, no right ellipsis
    if (showLeftEllipsis && !showRightEllipsis) {
      const rightItemCount = 3 + 2 * siblingCount;
      const rightRange = Array.from(
        { length: rightItemCount },
        (_, i) => totalPages - rightItemCount + i + 1
      );
      return [firstPageIndex, '...', ...rightRange];
    }

    // Case 3: Show both left and right ellipses
    if (showLeftEllipsis && showRightEllipsis) {
      const middleRange = Array.from(
        { length: rightSiblingIndex - leftSiblingIndex + 1 },
        (_, i) => leftSiblingIndex + i
      );
      return [firstPageIndex, '...', ...middleRange, '...', lastPageIndex];
    }

    return [];
  };

  const pages = generatePagination();

  const handleSetPage = (page: number) => {
    if (onSetPage && page !== currentPage) {
      onSetPage(page);
    }
  };

  return (
    <nav
      role="navigation"
      aria-label="Pagination Navigation"
      className={cn('flex items-center justify-center gap-1.5', className)}
    >
      <Button
        variant="outline"
        size="icon"
        className="h-9 w-9 border-slate-800 text-slate-300 hover:text-slate-100 hover:bg-slate-800 hover:border-slate-700"
        disabled={currentPage === 1}
        onClick={onPreviousPage}
        aria-label="Previous Page"
      >
        <ChevronLeft size={16} />
      </Button>

      {pages.map((page, index) => {
        if (page === '...') {
          return (
            <div
              key={`ellipsis-${index}`}
              className="flex h-9 w-9 items-center justify-center text-slate-500"
            >
              <MoreHorizontal size={16} />
            </div>
          );
        }

        const isCurrent = page === currentPage;

        return (
          <Button
            key={page}
            variant={isCurrent ? 'primary' : 'ghost'}
            size="icon"
            className={cn(
              'h-9 w-9 text-sm font-medium transition-all duration-200',
              isCurrent
                ? 'bg-blue-600/20 text-blue-400 hover:bg-blue-600/30 hover:text-blue-300 border border-blue-500/30'
                : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800'
            )}
            onClick={() => handleSetPage(page as number)}
            aria-current={isCurrent ? 'page' : undefined}
          >
            {page}
          </Button>
        );
      })}

      <Button
        variant="outline"
        size="icon"
        className="h-9 w-9 border-slate-800 text-slate-300 hover:text-slate-100 hover:bg-slate-800 hover:border-slate-700"
        disabled={currentPage === totalPages}
        onClick={onNextPage}
        aria-label="Next Page"
      >
        <ChevronRight size={16} />
      </Button>
    </nav>
  );
};

export default Pagination;
