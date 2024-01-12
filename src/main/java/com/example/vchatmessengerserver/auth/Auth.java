package com.example.vchatmessengerserver.auth;


import com.example.vchatmessengerserver.user.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public class Auth {
    public static User getUser(Authentication authentication) {

        if (authentication instanceof UsernamePasswordAuthenticationToken t) {
            return ((CustomUserDetails) t.getPrincipal()).getUser();
        }
        return null;
    }
}
