import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { X, Image as ImageIcon, Music, UploadCloud } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';

// Validation Schema (only basic text for now, files are handled separately due to complexity)
const cardSchema = z
  .object({
    type: z.enum(['BASIC', 'TYPE']),
    frontText: z.string().max(1000, 'Text is too long').optional(),
    backText: z.string().max(1000, 'Text is too long').optional(),
  })
  .refine((data) => data.frontText || data.backText, {
    message: 'At least front or back text must be provided if no media is used.',
    path: ['frontText'],
  });

type CardFormValues = z.infer<typeof cardSchema>;

interface CardFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  onSubmit: (data: any) => void;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  initialData?: any;
  isLoading?: boolean;
}

export const CardFormModal: React.FC<CardFormModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  initialData,
  isLoading = false,
}) => {
  const [frontImagePreview, setFrontImagePreview] = useState<string | null>(null);
  const [backImagePreview, setBackImagePreview] = useState<string | null>(null);
  const [frontAudioFile, setFrontAudioFile] = useState<File | null>(null);
  const [backAudioFile, setBackAudioFile] = useState<File | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<CardFormValues>({
    resolver: zodResolver(cardSchema),
    defaultValues: {
      type: initialData?.type || 'BASIC',
      frontText: initialData?.front?.text || '',
      backText: initialData?.back?.text || '',
    },
  });

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>, side: 'front' | 'back') => {
    const file = e.target.files?.[0];
    if (file) {
      const url = URL.createObjectURL(file);
      if (side === 'front') setFrontImagePreview(url);
      else setBackImagePreview(url);
    }
  };

  const handleAudioChange = (e: React.ChangeEvent<HTMLInputElement>, side: 'front' | 'back') => {
    const file = e.target.files?.[0];
    if (file) {
      if (side === 'front') setFrontAudioFile(file);
      else setBackAudioFile(file);
    }
  };

  const onFormSubmit = (data: CardFormValues) => {
    const submissionData = {
      ...data,
      frontImage: frontImagePreview, // In real app, pass the actual File object
      frontAudio: frontAudioFile,
      backImage: backImagePreview,
      backAudio: backAudioFile,
    };
    onSubmit(submissionData);
    if (!initialData) {
      // Optional: don't auto-close on create to allow mass creation
      reset();
      setFrontImagePreview(null);
      setBackImagePreview(null);
      setFrontAudioFile(null);
      setBackAudioFile(null);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-in fade-in overflow-y-auto">
      <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-3xl shadow-2xl animate-in zoom-in-95 my-8">
        <div className="flex items-center justify-between p-6 border-b border-slate-800 sticky top-0 bg-slate-900/95 backdrop-blur z-10 rounded-t-2xl">
          <h2 className="text-xl font-bold text-slate-100">
            {initialData ? 'Edit Card' : 'Add New Card'}
          </h2>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-slate-200 transition-colors p-1 rounded-lg hover:bg-slate-800"
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit(onFormSubmit)} className="p-6 flex flex-col gap-8">
          {/* Card Type Selector */}
          <div className="flex flex-col gap-2 border-b border-slate-800 pb-6">
            <label className="text-sm font-medium text-slate-300">Card Type</label>
            <select
              className="w-full md:w-1/2 bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-sm text-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500/50 appearance-none"
              {...register('type')}
            >
              <option value="BASIC">Basic (Standard Flashcard)</option>
              <option value="TYPE">Type Answer (Requires typing exact answer)</option>
            </select>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {/* FRONT SIDE */}
            <div className="flex flex-col gap-4">
              <div className="flex items-center gap-2 border-b border-slate-800 pb-2">
                <div className="w-6 h-6 rounded bg-blue-500/20 text-blue-400 flex items-center justify-center font-bold text-xs">
                  F
                </div>
                <h3 className="font-semibold text-slate-200">Front Side (Question)</h3>
              </div>

              <div className="flex flex-col gap-2">
                <label className="text-sm text-slate-400">Text Content</label>
                <textarea
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-3 text-sm text-slate-200 focus:outline-none focus:ring-2 focus:ring-blue-500/50 resize-none h-32"
                  placeholder="e.g. What is the capital of France?"
                  {...register('frontText')}
                />
              </div>

              {/* Front Media */}
              <div className="grid grid-cols-2 gap-4">
                <div className="flex flex-col gap-2">
                  <span className="text-xs text-slate-500 font-medium">Image</span>
                  <label className="relative flex flex-col items-center justify-center h-24 border-2 border-dashed border-slate-700 rounded-lg hover:bg-slate-800/50 hover:border-slate-500 transition-colors cursor-pointer overflow-hidden group">
                    {frontImagePreview ? (
                      <img
                        src={frontImagePreview}
                        alt="Front preview"
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <>
                        <ImageIcon
                          size={20}
                          className="text-slate-500 mb-1 group-hover:text-blue-400"
                        />
                        <span className="text-xs text-slate-400">Upload</span>
                      </>
                    )}
                    <input
                      type="file"
                      accept="image/*"
                      className="hidden"
                      onChange={(e) => handleImageChange(e, 'front')}
                    />
                  </label>
                </div>

                <div className="flex flex-col gap-2">
                  <span className="text-xs text-slate-500 font-medium">Audio</span>
                  <label className="relative flex flex-col items-center justify-center h-24 border-2 border-dashed border-slate-700 rounded-lg hover:bg-slate-800/50 hover:border-slate-500 transition-colors cursor-pointer group">
                    <Music
                      size={20}
                      className={
                        frontAudioFile
                          ? 'text-blue-400 mb-1'
                          : 'text-slate-500 mb-1 group-hover:text-blue-400'
                      }
                    />
                    <span className="text-xs text-slate-400 text-center px-1 truncate w-full">
                      {frontAudioFile ? frontAudioFile.name : 'Upload'}
                    </span>
                    <input
                      type="file"
                      accept="audio/*"
                      className="hidden"
                      onChange={(e) => handleAudioChange(e, 'front')}
                    />
                  </label>
                </div>
              </div>
            </div>

            {/* BACK SIDE */}
            <div className="flex flex-col gap-4">
              <div className="flex items-center gap-2 border-b border-slate-800 pb-2">
                <div className="w-6 h-6 rounded bg-green-500/20 text-green-400 flex items-center justify-center font-bold text-xs">
                  B
                </div>
                <h3 className="font-semibold text-slate-200">Back Side (Answer)</h3>
              </div>

              <div className="flex flex-col gap-2">
                <label className="text-sm text-slate-400">Text Content</label>
                <textarea
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-3 text-sm text-slate-200 focus:outline-none focus:ring-2 focus:ring-green-500/50 resize-none h-32"
                  placeholder="e.g. Paris"
                  {...register('backText')}
                />
              </div>

              {/* Back Media */}
              <div className="grid grid-cols-2 gap-4">
                <div className="flex flex-col gap-2">
                  <span className="text-xs text-slate-500 font-medium">Image</span>
                  <label className="relative flex flex-col items-center justify-center h-24 border-2 border-dashed border-slate-700 rounded-lg hover:bg-slate-800/50 hover:border-slate-500 transition-colors cursor-pointer overflow-hidden group">
                    {backImagePreview ? (
                      <img
                        src={backImagePreview}
                        alt="Back preview"
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <>
                        <ImageIcon
                          size={20}
                          className="text-slate-500 mb-1 group-hover:text-green-400"
                        />
                        <span className="text-xs text-slate-400">Upload</span>
                      </>
                    )}
                    <input
                      type="file"
                      accept="image/*"
                      className="hidden"
                      onChange={(e) => handleImageChange(e, 'back')}
                    />
                  </label>
                </div>

                <div className="flex flex-col gap-2">
                  <span className="text-xs text-slate-500 font-medium">Audio</span>
                  <label className="relative flex flex-col items-center justify-center h-24 border-2 border-dashed border-slate-700 rounded-lg hover:bg-slate-800/50 hover:border-slate-500 transition-colors cursor-pointer group">
                    <Music
                      size={20}
                      className={
                        backAudioFile
                          ? 'text-green-400 mb-1'
                          : 'text-slate-500 mb-1 group-hover:text-green-400'
                      }
                    />
                    <span className="text-xs text-slate-400 text-center px-1 truncate w-full">
                      {backAudioFile ? backAudioFile.name : 'Upload'}
                    </span>
                    <input
                      type="file"
                      accept="audio/*"
                      className="hidden"
                      onChange={(e) => handleAudioChange(e, 'back')}
                    />
                  </label>
                </div>
              </div>
            </div>
          </div>

          {errors.frontText && (
            <p className="text-sm text-red-500 bg-red-500/10 p-3 rounded-lg border border-red-500/20">
              {errors.frontText.message}
            </p>
          )}

          <div className="flex justify-end gap-3 pt-6 border-t border-slate-800 mt-2">
            <Button type="button" variant="outline" onClick={onClose} disabled={isLoading}>
              Cancel
            </Button>
            <Button type="submit" isLoading={isLoading} className="gap-2">
              <UploadCloud size={18} />
              {initialData ? 'Save Card' : 'Add Card'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};
