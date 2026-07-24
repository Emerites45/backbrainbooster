package com.example.back.util;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class OtpGeneratorUtil {

    private static final SecureRandom random = new SecureRandom();

    /**
     * Génère un code OTP numérique de la longueur souhaitée (ex: 6 chiffres)
     */
    public String generateOtp(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}