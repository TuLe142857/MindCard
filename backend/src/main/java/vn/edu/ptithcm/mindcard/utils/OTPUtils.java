package vn.edu.ptithcm.mindcard.utils;

import java.security.SecureRandom;

public class OTPUtils {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a random alphanumeric string to use as an OTP. The generated
     * value includes numbers and uppercase letters.
     *
     * @param length the length of the requested OTP. Must be > 0.
     *
     * @return the generated OTP string.
     *
     * @throws IllegalArgumentException if length <= 0.
     */
    public static String generateOTP(int length) throws IllegalArgumentException {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be greater than zero");
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            builder.append(CHARACTERS.charAt(index));
        }
        return builder.toString();
    }
}
