package com.example.vchatmessengerserver.auth;

import com.example.vchatmessengerserver.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.example.vchatmessengerserver.user.User;
import com.example.vchatmessengerserver.user.UserRepository;

import java.util.Optional;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String nickname) throws UserNotFoundException {
        Optional<User> user = userRepository.findByNickname(nickname);
        return user.map(CustomUserDetails::new).orElse(null);
    }
}
