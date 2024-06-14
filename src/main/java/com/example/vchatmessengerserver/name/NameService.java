package com.example.vchatmessengerserver.name;

public class NameService {
    public static int ok = 200;
    public static int nameError = 500;

    public static int checkName(String name) {
        if (name.replace(" ", "").isEmpty() || name.length() > 30) {
            return nameError;
        }
        return ok;
    }
}
