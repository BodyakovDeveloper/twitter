package koval.proxyseller.twitter.service.comment

import koval.proxyseller.twitter.dto.PageResponseDto
import koval.proxyseller.twitter.dto.comment.CommentCreateRequestDto
import koval.proxyseller.twitter.dto.comment.CommentDto
import koval.proxyseller.twitter.dto.comment.CommentUpdateRequestDto
import koval.proxyseller.twitter.dto.post.PostDto
import koval.proxyseller.twitter.exception.EntityNotFoundException
import koval.proxyseller.twitter.exception.ResourceOwnershipException
import koval.proxyseller.twitter.mapper.comment.CommentMapper
import koval.proxyseller.twitter.mapper.post.PostMapper
import koval.proxyseller.twitter.model.comment.Comment
import koval.proxyseller.twitter.model.post.Post
import koval.proxyseller.twitter.repository.comment.CommentRepository
import koval.proxyseller.twitter.repository.post.PostRepository
import koval.proxyseller.twitter.security.SecurityUtil
import koval.proxyseller.twitter.service.comment.impl.CommentServiceImpl
import koval.proxyseller.twitter.service.post.PostService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import spock.lang.Specification
import spock.lang.Subject

class CommentServiceImplSpec extends Specification {

    CommentRepository commentRepository = Mock()
    CommentMapper commentMapper = Mock()
    PostService postService = Mock()
    PostRepository postRepository = Mock()
    PostMapper postMapper = Mock()

    @Subject
    CommentServiceImpl commentService = new CommentServiceImpl(commentRepository, commentMapper, postService, postRepository, postMapper)

    def "should create a comment successfully"() {
        given: "a comment creation request"
        def postId = "post123"
        def request = new CommentCreateRequestDto(
                content: "Great post!",
                postId: postId
        )
        def post = new Post(id: postId)
        def comment = new Comment(id: "comment123", content: "Great post!", postId: postId)
        def commentDto = new CommentDto(id: "comment123", content: "Great post!", postId: postId)
        def postDto = new PostDto(id: postId)

        when: "creating the comment"
        def result = commentService.createComment(request)

        then: "comment should be created"
        1 * postRepository.findById(postId) >> Optional.of(post)
        1 * commentMapper.toModel(request) >> comment
        1 * commentRepository.insert(comment) >> comment
        1 * postMapper.toDto(post) >> postDto
        1 * postService.updatePost(postDto)
        1 * commentMapper.toDto(comment) >> commentDto
        result != null
        result.id == "comment123"
    }

    def "should throw EntityNotFoundException when post not found"() {
        given: "a comment request for non-existent post"
        def request = new CommentCreateRequestDto(
                content: "Comment",
                postId: "nonexistent"
        )

        when: "creating the comment"
        commentService.createComment(request)

        then: "should throw EntityNotFoundException"
        1 * postRepository.findById("nonexistent") >> Optional.empty()
        thrown(EntityNotFoundException)
    }

    def "should get comments by post id"() {
        given: "a post id"
        def postId = "post123"
        def comments = [
                new Comment(id: "1", postId: postId, content: "Comment 1"),
                new Comment(id: "2", postId: postId, content: "Comment 2")
        ]
        def commentDtos = [
                new CommentDto(id: "1", content: "Comment 1"),
                new CommentDto(id: "2", content: "Comment 2")
        ]

        when: "getting comments by post id"
        def result = commentService.getCommentsByPostId(postId)

        then: "comments should be returned"
        1 * commentRepository.findAllByPostIdAndIsDeletedFalse(postId) >> comments
        2 * commentMapper.toDto(_) >> { Comment c -> commentDtos[comments.indexOf(c)] }
        result != null
        result.size() == 2
    }

    def "should get comments by post id with pagination"() {
        given: "a post id and pageable"
        def postId = "post123"
        def pageable = PageRequest.of(0, 10)
        def comments = [new Comment(id: "1", postId: postId)]
        def page = new PageImpl<>(comments, pageable, 1)

        when: "getting comments by post id"
        def result = commentService.getCommentsByPostId(postId, pageable)

        then: "paginated comments should be returned"
        1 * commentRepository.findAllByPostIdAndIsDeletedFalse(postId, pageable) >> page
        1 * commentMapper.toDto(_) >> new CommentDto(id: "1")
        result != null
        result.content.size() == 1
    }

    def "should get comments by user id"() {
        given: "a user id"
        def userId = "user123"
        def comments = [
                new Comment(id: "1", userId: userId, content: "Comment 1"),
                new Comment(id: "2", userId: userId, content: "Comment 2")
        ]
        def commentDtos = [
                new CommentDto(id: "1", content: "Comment 1"),
                new CommentDto(id: "2", content: "Comment 2")
        ]

        when: "getting comments by user id"
        def result = commentService.getCommentsByUserId(userId)

        then: "user comments should be returned"
        1 * commentRepository.findAllByUserIdAndIsDeletedFalse(userId) >> comments
        2 * commentMapper.toDto(_) >> { Comment c -> commentDtos[comments.indexOf(c)] }
        result != null
        result.size() == 2
    }

    def "should get comment by id"() {
        given: "a comment id"
        def commentId = "comment123"
        def comment = new Comment(id: commentId, content: "Test comment")
        def commentDto = new CommentDto(id: commentId, content: "Test comment")

        when: "getting comment by id"
        def result = commentService.getCommentById(commentId)

        then: "comment should be returned"
        1 * commentRepository.findByIdAndIsDeletedFalse(commentId) >> Optional.of(comment)
        1 * commentMapper.toDto(comment) >> commentDto
        result != null
        result.id == commentId
    }

    def "should update comment successfully when user is owner"() {
        given: "a comment id and update request"
        def commentId = "comment123"
        def userId = "user123"
        def comment = new Comment(id: commentId, userId: userId, content: "Old content", postId: "post123")
        def updateRequest = new CommentUpdateRequestDto(content: "New content")
        def post = new Post(id: "post123")
        def postDto = new PostDto(id: "post123")
        def updatedComment = new Comment(id: commentId, content: "New content")
        def commentDto = new CommentDto(id: commentId, content: "New content")

        when: "updating the comment"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> userId }
        def result = commentService.updateComment(commentId, updateRequest)

        then: "comment should be updated"
        1 * commentRepository.findByIdAndIsDeletedFalse(commentId) >> Optional.of(comment)
        1 * commentRepository.save(_) >> { Comment c -> assert c.content == "New content"; updatedComment }
        1 * postRepository.findById("post123") >> Optional.of(post)
        1 * postMapper.toDto(post) >> postDto
        1 * postService.updatePost(postDto)
        1 * commentMapper.toDto(updatedComment) >> commentDto
        result != null
        result.content == "New content"
    }

    def "should throw ResourceOwnershipException when user is not owner"() {
        given: "a comment owned by another user"
        def commentId = "comment123"
        def comment = new Comment(id: commentId, userId: "otherUser", content: "Content")
        def updateRequest = new CommentUpdateRequestDto(content: "New content")
        def currentUserId = "currentUser"

        when: "updating the comment"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> currentUserId }
        commentService.updateComment(commentId, updateRequest)

        then: "should throw ResourceOwnershipException"
        1 * commentRepository.findByIdAndIsDeletedFalse(commentId) >> Optional.of(comment)
        thrown(ResourceOwnershipException)
    }

    def "should delete comment successfully when user is owner"() {
        given: "a comment id"
        def commentId = "comment123"
        def userId = "user123"
        def comment = new Comment(id: commentId, userId: userId, isDeleted: false)

        when: "deleting the comment"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> userId }
        commentService.deleteComment(commentId)

        then: "comment should be marked as deleted"
        1 * commentRepository.findByIdAndIsDeletedFalse(commentId) >> Optional.of(comment)
        1 * commentRepository.save(_) >> { Comment c -> assert c.isDeleted == true; c }
    }

    def "should throw ResourceOwnershipException when deleting comment not owned by user"() {
        given: "a comment owned by another user"
        def commentId = "comment123"
        def comment = new Comment(id: commentId, userId: "otherUser")
        def currentUserId = "currentUser"

        when: "deleting the comment"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> currentUserId }
        commentService.deleteComment(commentId)

        then: "should throw ResourceOwnershipException"
        1 * commentRepository.findByIdAndIsDeletedFalse(commentId) >> Optional.of(comment)
        thrown(ResourceOwnershipException)
    }
}

