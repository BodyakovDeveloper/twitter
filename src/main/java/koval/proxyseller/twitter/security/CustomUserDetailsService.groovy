package koval.proxyseller.twitter.security

import koval.proxyseller.twitter.repository.user.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find user by username!"));
    }
}

