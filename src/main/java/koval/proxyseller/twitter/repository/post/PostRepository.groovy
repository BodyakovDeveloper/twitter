package koval.proxyseller.twitter.repository.post

import koval.proxyseller.twitter.model.post.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface PostRepository extends MongoRepository<Post, String> {
    Optional<Post> findByIdAndIsDeletedFalse(String id)
    List<Post> findAllByIsDeletedFalse()
    Page<Post> findAllByIsDeletedFalse(Pageable pageable)
    List<Post> findAllByUserIdAndIsDeletedFalse(String userId)
    Page<Post> findAllByUserIdAndIsDeletedFalse(String userId, Pageable pageable)
    List<Post> findAllByUserIdInAndIsDeletedFalse(Set<String> userIds)
    Page<Post> findAllByUserIdInAndIsDeletedFalse(Set<String> userIds, Pageable pageable)
}