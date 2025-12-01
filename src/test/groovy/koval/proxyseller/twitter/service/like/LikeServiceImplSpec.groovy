package koval.proxyseller.twitter.service.like

import koval.proxyseller.twitter.dto.like.LikeDto
import koval.proxyseller.twitter.dto.like.LikePostRequestDto
import koval.proxyseller.twitter.dto.like.UnlikePostRequestDto
import koval.proxyseller.twitter.dto.post.PostDto
import koval.proxyseller.twitter.exception.DuplicateEntityException
import koval.proxyseller.twitter.exception.EntityNotFoundException
import koval.proxyseller.twitter.mapper.like.LikeMapper
import koval.proxyseller.twitter.mapper.post.PostMapper
import koval.proxyseller.twitter.model.like.Like
import koval.proxyseller.twitter.model.post.Post
import koval.proxyseller.twitter.repository.like.LikeRepository
import koval.proxyseller.twitter.security.SecurityUtil
import koval.proxyseller.twitter.service.like.impl.LikeServiceImpl
import koval.proxyseller.twitter.service.post.PostService
import spock.lang.Specification
import spock.lang.Subject

class LikeServiceImplSpec extends Specification {

    LikeRepository likeRepository = Mock()
    PostService postService = Mock()
    LikeMapper likeMapper = Mock()
    PostMapper postMapper = Mock()

    @Subject
    LikeServiceImpl likeService = new LikeServiceImpl(likeRepository, postService, likeMapper, postMapper)

    def "should like a post successfully"() {
        given: "a like request"
        def postId = "post123"
        def userId = "user123"
        def request = new LikePostRequestDto(postId: postId)
        def like = new Like(id: "like123", postId: postId, userId: userId)
        def likeDto = new LikeDto(id: "like123", postId: postId, userId: userId)
        def post = new Post(id: postId, likes: [] as Set)
        def postDto = new PostDto(id: postId)

        when: "liking the post"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> userId }
        def result = likeService.likePost(request)

        then: "like should be created"
        1 * likeRepository.existsByPostIdAndUserIdAndIsDeletedFalse(postId, userId) >> false
        1 * likeMapper.toModel(request) >> like
        1 * likeRepository.save(like) >> like
        1 * likeMapper.toDto(like) >> likeDto
        1 * postService.getPostModelById(postId) >> post
        1 * postMapper.toDto(post) >> postDto
        1 * postService.updatePost(postDto)
        result != null
        result.id == "like123"
    }

    def "should throw DuplicateEntityException when user already liked the post"() {
        given: "a like request for already liked post"
        def postId = "post123"
        def userId = "user123"
        def request = new LikePostRequestDto(postId: postId)

        when: "liking the post again"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> userId }
        likeService.likePost(request)

        then: "should throw DuplicateEntityException"
        1 * likeRepository.existsByPostIdAndUserIdAndIsDeletedFalse(postId, userId) >> true
        thrown(DuplicateEntityException)
    }

    def "should unlike a post successfully"() {
        given: "an unlike request"
        def postId = "post123"
        def userId = "user123"
        def request = new UnlikePostRequestDto(postId: postId)
        def like = new Like(id: "like123", postId: postId, userId: userId, isDeleted: false)
        def post = new Post(id: postId, likes: [like] as Set)
        def postDto = new PostDto(id: postId)

        when: "unliking the post"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> userId }
        likeService.unlikePost(request)

        then: "like should be marked as deleted"
        1 * likeRepository.findByPostIdAndUserIdAndIsDeletedFalse(postId, userId) >> Optional.of(like)
        1 * likeRepository.save(_) >> { Like l -> assert l.isDeleted == true; l }
        1 * postService.getPostModelById(postId) >> post
        1 * postMapper.toDto(post) >> postDto
        1 * postService.updatePost(postDto)
    }

    def "should throw EntityNotFoundException when like not found"() {
        given: "an unlike request for non-existent like"
        def postId = "post123"
        def userId = "user123"
        def request = new UnlikePostRequestDto(postId: postId)

        when: "unliking the post"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> userId }
        likeService.unlikePost(request)

        then: "should throw EntityNotFoundException"
        1 * likeRepository.findByPostIdAndUserIdAndIsDeletedFalse(postId, userId) >> Optional.empty()
        thrown(EntityNotFoundException)
    }

    def "should get likes count by post id"() {
        given: "a post id"
        def postId = "post123"
        def likes = [
                new Like(id: "1", postId: postId, isDeleted: false),
                new Like(id: "2", postId: postId, isDeleted: false),
                new Like(id: "3", postId: postId, isDeleted: true) // deleted like
        ]

        when: "getting likes count"
        def result = likeService.getLikesCount(postId)

        then: "count should be returned"
        1 * likeRepository.findByPostId(postId) >> likes
        result == 2 // only non-deleted likes
    }

    def "should get likes by post id"() {
        given: "a post id"
        def postId = "post123"
        def like1 = new Like(id: "1", postId: postId)
        def like2 = new Like(id: "2", postId: postId)
        def post = new Post(id: postId, likes: [like1, like2] as Set)
        def likeDtos = [
                new LikeDto(id: "1", postId: postId),
                new LikeDto(id: "2", postId: postId)
        ]

        when: "getting likes by post id"
        def result = likeService.getLikesByPostId(postId)

        then: "likes should be returned"
        1 * postService.getPostModelById(postId) >> post
        2 * likeMapper.toDto(_) >> { Like l -> likeDtos.find { it.id == l.id } }
        result != null
        result.size() == 2
    }
}

