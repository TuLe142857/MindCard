package vn.edu.ptithcm.mindcard.repository.projection;

import vn.edu.ptithcm.mindcard.entity.Card;
import vn.edu.ptithcm.mindcard.entity.UserCardProgress;

/**
 * Interface-based projection to replace Object[] when fetching 
 * out-of-sync cards along with their corresponding user progress.
 */
public interface CardSyncProjection {
    Card getCard();
    UserCardProgress getProgress();
}
