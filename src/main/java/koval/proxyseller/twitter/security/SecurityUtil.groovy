package koval.proxyseller.twitter.security

import groovy.util.logging.Slf4j
import koval.proxyseller.twitter.model.user.User
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Slf4j
@Component
class SecurityUtil {

    static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found")
        }
        Object principal = authentication.getPrincipal()
        if (principal instanceof User) {
            return ((User) principal).getId()
        }
        throw new IllegalStateException("Principal is not a User instance")
    }

    static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication()
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found")
        }
        return authentication.getName()
    }

    static boolean isCurrentUser(String userId) {
        try {
            return getCurrentUserId().equals(userId)
        } catch (Exception e) {
            return false
        }
    }
}
