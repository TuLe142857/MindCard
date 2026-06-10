/**
 * Enum representing the type of a flashcard.
 * - `BASIC`: Standard front/back card.
 * - `TYPE`: Card that requires typing the answer.
 */
export type CardType = 'BASIC' | 'TYPE';

/**
 * Request payload for updating an existing Card's text content.
 */
export interface CardUpdateRequest {
  /**
   * Updated card type.
   * - `BASIC`: Standard front/back card
   * - `TYPE`: Card that requires typing the answer
   */
  type?: CardType;
  /** Updated text for the front of the card */
  frontText?: string;
  /** Updated text for the back of the card */
  backText?: string;
}

/**
 * Request payload for submitting a study review score for a card.
 * Uses the SuperMemo-2 algorithm to calculate the next review date.
 */
export interface CardReviewRequest {
  /**
   * Quality of the review score representing user's recall performance.
   * - `0`: Complete blackout, no recall at all
   * - `1`: Incorrect, but upon seeing the answer, remembered
   * - `2`: Incorrect, but the answer seemed easy to recall
   * - `3`: Correct, but with serious difficulty
   * - `4`: Correct, after some hesitation
   * - `5`: Perfect, instant recall
   */
  quality: number;
}

/**
 * Represents a media file slot on a card.
 * Used for front-image, front-audio, back-image, and back-audio uploads.
 */
export type CardMediaSlot = 'front-image' | 'front-audio' | 'back-image' | 'back-audio';
