package koval.proxyseller.twitter.repository.user


import koval.proxyseller.twitter.model.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByIdAndIsDeletedFalse(String id)

    Optional<User> findByEmailAndIsDeletedFalse(String email)

    Optional<User> findByUsernameAndIsDeletedFalse(String username)

    List<User> findAllByIsDeletedFalse()

    Page<User> findAllByIsDeletedFalse(Pageable pageable)

    List<User> findAllByIdInAndIsDeletedFalse(Set<String> ids)

    Page<User> findAllByIdInAndIsDeletedFalse(Set<String> ids, Pageable pageable)
}