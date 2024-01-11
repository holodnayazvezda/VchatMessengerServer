package com.example.vchatmessengerserver.password;

import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    public static int ok = 200;
    public static int lengthError = 333;
    public static int noNumberError = 401;
    public static int noLowercaseLetter = 402;
    public static int noUppercaseLetter = 403;
    public static int noSpecialSymbolError = 404;
    public static int contentError = 222;
    public static int matchError = 500;

    public static int checkCorrectness(String password) {
        if (password.length() < 8 || password.length() > 30) {
            return lengthError;
        }
        if (!password.matches(".*\\d.*")) {
            return noNumberError;
        }
        if (!password.matches(".*[a-z].*")) {
            return noLowercaseLetter;
        }
        if (!password.matches(".*[A-Z].*")) {
            return noUppercaseLetter;
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return noSpecialSymbolError;
        }
        if (!password.matches("[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]*")) {
            return contentError;
        }
        return ok;
    }

    public static boolean checkConfirmation(String password, String password_confirmation) {
        return password.equals(password_confirmation);
    }

    public static int checkEverything(String password1, String password2) {
        int res = checkCorrectness(password1);
        if (res == ok) {
            if (checkConfirmation(password1, password2)) {
                return ok;
            } else {
                return matchError;
            }
        } else {
            return res;
        }
    }
}
