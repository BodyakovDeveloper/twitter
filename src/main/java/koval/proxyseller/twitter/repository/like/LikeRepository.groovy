package koval.proxyseller.twitter.repository.like

import koval.proxyseller.twitter.model.like.Like
import org.springframework.data.mongodb.repository.MongoRepository

interface LikeRepository extends MongoRepository<Like, String> {
    List<Like> findAllByPostIdAndUserId(String postId, String userId)
    List<Like> findByPostId(String id)
    boolean existsByPostIdAndUserIdAndIsDeletedFalse(String postId, String userId)
    Optional<Like> findByPostIdAndUserIdAndIsDeletedFalse(String postId, String userId)
}