package vn.edu.ptithcm.mindcard.service.algorithm;

import lombok.Builder;

import java.time.Instant;

public interface SpaceRepetitionAlgorithm {


    /**
     * @param easinessFactor (double)
     * @param interval (days)
     * @param repetitions (times)
     * @param nextReview (timestamp)
     */
    @Builder
    record ScheduleResult(
            double easinessFactor,
            int interval,
            int repetitions,
            Instant nextReview
    )
    {}

    public ScheduleResult calculate(double easinessFactor, int interval, int repetitions, int quality);
}
