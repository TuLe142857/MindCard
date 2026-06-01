package vn.edu.ptithcm.mindcard.service.algorithm;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class SpaceRepetitionImpl implements SpaceRepetitionAlgorithm {
    private static final double MIN_EASINESS_FACTOR = 1.3;
    private static final double INITIAL_EASINESS_FACTOR = 2.5;

    @Override
    public ScheduleResult calculate(double easinessFactor, int interval, int repetitions, int quality)
            throws IllegalArgumentException
    {
        if (!(quality >= 0 && quality <= 5)){
            throw new IllegalArgumentException("quality must in range [0, 5]");
        }

        double newEF = calculateEF(easinessFactor, quality);
        int newInterval = calculateInterval(interval, repetitions, quality, newEF);
        int newRepetitions = isSuccess(quality) ? (repetitions + 1) : 0;
        Instant nextReview = Instant.now().plus(newInterval, ChronoUnit.DAYS);

        return ScheduleResult.builder()
                .easinessFactor(newEF)
                .interval(newInterval)
                .repetitions(newRepetitions)
                .nextReview(nextReview)
                .build();
    }

    private boolean isSuccess(int quality){
        return quality >= 3;
    }

    private double calculateEF(double easinessFactor, int quality) {
        double newEF = easinessFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
        return Math.max(MIN_EASINESS_FACTOR, newEF);
    }

    private int calculateInterval(int interval, int repetitions, int quality, double newEF) {
        if (quality < 3) return 1;

        return switch (repetitions) {
            case 0 -> 1;
            case 1 -> 6;
            default -> (int) Math.round(interval * newEF);
        };
    }

}
