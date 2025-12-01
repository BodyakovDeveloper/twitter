package koval.proxyseller.twitter.model.user

import koval.proxyseller.twitter.model.enumeration.Role
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import java.time.Instant

@Document(collection = "users")
class User implements UserDetails, Serializable {
    @Id
    String id
    String firstName
    String lastName
    @Indexed(unique = true)
    String username
    @Indexed(unique = true)
    String email
    String password
    Integer age
    Set<String> following = new HashSet<>()
    Set<String> followers = new HashSet<>()
    Role role = Role.ROLE_USER
    boolean isDeleted = false
    Instant createdAt
    Instant updatedAt

    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()))
    }

    @Override
    boolean isAccountNonExpired() {
        return true
    }

    @Override
    boolean isAccountNonLocked() {
        return true
    }

    @Override
    boolean isCredentialsNonExpired() {
        return true
    }

    @Override
    boolean isEnabled() {
        return !isDeleted
    }

    // Method to follow a user
    void followUser(String id) {
        following.add(id)
    }

    // Method to unfollow a user
    void unfollowUser(String id) {
        following.remove(id)
    }

    // Method to add a follower
    void addFollower(String id) {
        followers.add(id)
    }

    // Method to remove a follower
    void removeFollower(String id) {
        followers.remove(id)
    }
}
