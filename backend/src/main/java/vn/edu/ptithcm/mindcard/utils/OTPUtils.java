package vn.edu.ptithcm.mindcard.utils;

import java.security.SecureRandom;

public class OTPUtils {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final SecureRandom random = new SecureRandom();

    public static String generateOTP(int length){
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++){
            int index = random.nextInt(CHARACTERS.length());
            builder.append(CHARACTERS.charAt(index));
        }
        return builder.toString();
    }
}
