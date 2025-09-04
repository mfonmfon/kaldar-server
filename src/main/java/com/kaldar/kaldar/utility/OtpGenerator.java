package com.kaldar.kaldar.utility;

import java.security.SecureRandom;

public class OtpGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateOtp(int digits) {
        StringBuilder stringBuilder = new StringBuilder(digits);
        for (int index = 0; index < digits; index++){
            stringBuilder.append(secureRandom.nextInt(10));
        }
        return stringBuilder.toString();
    }
}
