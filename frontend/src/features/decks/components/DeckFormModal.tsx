import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { X } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { Input } from '@/shared/components/ui/Input';
import type { DeckCreateRequest } from '../types';

const deckSchema = z.object({
  name: z.string().min(3, 'Name must be at least 3 characters').max(100, 'Name is too long'),
  topicId: z.coerce.number().min(1, 'Please select a topic'),
  description: z.string().max(500, 'Description is too long').optional(),
  visibility: z.enum(['PUBLIC', 'PRIVATE']),
});

type DeckFormValues = z.infer<typeof deckSchema>;

interface DeckFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  initialData?: Partial<DeckFormValues>;
  onSubmit: (data: DeckFormValues) => void;
  isLoading?: boolean;
}

// Mock topics for UI
const MOCK_TOPICS = [
  { id: 1, name: 'Programming' },
  { id: 2, name: 'Languages' },
  { id: 3, name: 'History' },
  { id: 4, name: 'Science' },
  { id: 5, name: 'Technology' },
];

export const DeckFormModal: React.FC<DeckFormModalProps> = ({
  isOpen,
  onClose,
  initialData,
  onSubmit,
  isLoading = false,
}) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset
  } = useForm<DeckFormValues>({
    resolver: zodResolver(deckSchema),
    defaultValues: {
      name: initialData?.name || '',
      topicId: initialData?.topicId || 0,
      description: initialData?.description || '',
      visibility: initialData?.visibility || 'PRIVATE',
    },
  });

  // Reset form when opening with new data
  React.useEffect(() => {
    if (isOpen) {
      reset({
        name: initialData?.name || '',
        topicId: initialData?.topicId || 0,
        description: initialData?.description || '',
        visibility: initialData?.visibility || 'PRIVATE',
      });
    }
  }, [isOpen, initialData, reset]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-in fade-in">
      <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-lg shadow-2xl animate-in zoom-in-95">
        <div className="flex items-center justify-between p-6 border-b border-slate-800">
          <h2 className="text-xl font-bold text-slate-100">
            {initialData ? 'Edit Deck' : 'Create New Deck'}
          </h2>
          <button 
            onClick={onClose}
            className="text-slate-400 hover:text-slate-200 transition-colors p-1 rounded-lg hover:bg-slate-800"
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="p-6 flex flex-col gap-5">
          <Input
            label="Deck Name"
            placeholder="e.g., JLPT N3 Vocabulary"
            error={errors.name?.message}
            {...register('name')}
          />

          <div className="flex flex-col gap-1.5">
            <label className="text-sm font-medium text-slate-300">Topic</label>
            <select
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-sm text-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 transition-all"
              {...register('topicId')}
            >
              <option value={0} disabled>Select a topic</option>
              {MOCK_TOPICS.map((topic) => (
                <option key={topic.id} value={topic.id}>{topic.name}</option>
              ))}
            </select>
            {errors.topicId && (
              <p className="text-xs text-red-500 mt-1">{errors.topicId.message}</p>
            )}
          </div>

          <div className="flex flex-col gap-1.5">
            <label className="text-sm font-medium text-slate-300">Description (Optional)</label>
            <textarea
              placeholder="Briefly describe what this deck is about..."
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-sm text-slate-200 placeholder:text-slate-500 focus:outline-none focus:ring-2 focus:ring-blue-500/50 focus:border-blue-500 transition-all resize-none h-24"
              {...register('description')}
            />
            {errors.description && (
              <p className="text-xs text-red-500 mt-1">{errors.description.message}</p>
            )}
          </div>

          <div className="flex flex-col gap-1.5">
            <label className="text-sm font-medium text-slate-300">Visibility</label>
            <div className="grid grid-cols-2 gap-3">
              <label className="relative flex items-center justify-center gap-2 p-3 border border-slate-800 rounded-lg cursor-pointer bg-slate-950 hover:bg-slate-800/50 transition-colors has-[:checked]:border-blue-500 has-[:checked]:bg-blue-500/10">
                <input 
                  type="radio" 
                  value="PRIVATE" 
                  className="sr-only"
                  {...register('visibility')}
                />
                <span className="text-sm font-medium text-slate-300">Private</span>
              </label>
              <label className="relative flex items-center justify-center gap-2 p-3 border border-slate-800 rounded-lg cursor-pointer bg-slate-950 hover:bg-slate-800/50 transition-colors has-[:checked]:border-blue-500 has-[:checked]:bg-blue-500/10">
                <input 
                  type="radio" 
                  value="PUBLIC" 
                  className="sr-only"
                  {...register('visibility')}
                />
                <span className="text-sm font-medium text-slate-300">Public</span>
              </label>
            </div>
            {errors.visibility && (
              <p className="text-xs text-red-500 mt-1">{errors.visibility.message}</p>
            )}
            <p className="text-xs text-slate-500 mt-1">
              Public decks can be discovered and saved by other users.
            </p>
          </div>

          <div className="flex justify-end gap-3 mt-4 pt-4 border-t border-slate-800">
            <Button type="button" variant="outline" onClick={onClose} disabled={isLoading}>
              Cancel
            </Button>
            <Button type="submit" isLoading={isLoading}>
              {initialData ? 'Save Changes' : 'Create Deck'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};
