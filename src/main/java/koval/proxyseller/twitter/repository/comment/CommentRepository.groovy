package koval.proxyseller.twitter.repository.comment

import koval.proxyseller.twitter.model.comment.Comment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface CommentRepository extends MongoRepository<Comment, String> {
    Optional<Comment> findByIdAndIsDeletedFalse(String id)
    List<Comment> findAllByPostIdAndIsDeletedFalse(String postId)
    Page<Comment> findAllByPostIdAndIsDeletedFalse(String postId, Pageable pageable)
    List<Comment> findAllByUserIdAndIsDeletedFalse(String userId)
    Page<Comment> findAllByUserIdAndIsDeletedFalse(String userId, Pageable pageable)
}