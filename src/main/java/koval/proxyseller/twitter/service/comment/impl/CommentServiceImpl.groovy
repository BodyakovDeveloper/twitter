package koval.proxyseller.twitter.service.comment.impl

import koval.proxyseller.twitter.dto.PageResponseDto
import koval.proxyseller.twitter.dto.comment.CommentCreateRequestDto
import koval.proxyseller.twitter.dto.comment.CommentDto
import koval.proxyseller.twitter.dto.comment.CommentUpdateRequestDto
import koval.proxyseller.twitter.exception.EntityNotFoundException
import koval.proxyseller.twitter.mapper.comment.CommentMapper
import koval.proxyseller.twitter.mapper.post.PostMapper
import koval.proxyseller.twitter.model.comment.Comment
import koval.proxyseller.twitter.model.post.Post
import koval.proxyseller.twitter.repository.comment.CommentRepository
import koval.proxyseller.twitter.repository.post.PostRepository
import koval.proxyseller.twitter.security.SecurityUtil
import koval.proxyseller.twitter.service.comment.CommentService
import koval.proxyseller.twitter.service.post.PostService
import groovy.util.logging.Slf4j
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

import java.util.stream.Collectors

@Slf4j
@Service
class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository
    private final CommentMapper commentMapper
    private final PostService postService
    private final PostRepository postRepository
    private final PostMapper postMapper

    CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper, PostService postService, PostRepository postRepository, PostMapper postMapper) {
        this.commentRepository = commentRepository
        this.commentMapper = commentMapper
        this.postService = postService
        this.postRepository = postRepository
        this.postMapper = postMapper
    }

    @Override
    @CacheEvict(value = ["comments", "posts"], key = "'post_' + #commentCreateRequestDto.postId")
    CommentDto createComment(CommentCreateRequestDto commentCreateRequestDto) {
        Post post = postRepository.findById(commentCreateRequestDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found"))

        Comment comment = commentMapper.toModel(commentCreateRequestDto)

        log.info("Comment service: creating comment, request: $commentCreateRequestDto")
        commentRepository.insert(comment)
        post.addComment(comment)
        postService.updatePost(postMapper.toDto(post))

        return commentMapper.toDto(comment)
    }

    @Override
    List<CommentDto> getCommentsByPostId(String postId) {
        return commentRepository.findAllByPostIdAndIsDeletedFalse(postId).stream()
                .map {commentMapper.toDto(it)}
                .collect(Collectors.toList())
    }

    @Override
    @Cacheable(value = "comments", key = "'post_' + #postId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    PageResponseDto<CommentDto> getCommentsByPostId(String postId, Pageable pageable) {
        log.info("Comment service: getting comments by post id with pagination: postId=${postId}, page=${pageable.pageNumber}, size=${pageable.pageSize}")
        Page<Comment> commentPage = commentRepository.findAllByPostIdAndIsDeletedFalse(postId, pageable)
        List<CommentDto> content = commentPage.content.stream()
                .map {commentMapper.toDto(it)}
                .collect(Collectors.toList())
        
        return new PageResponseDto<>(
                content,
                commentPage.number,
                commentPage.size,
                commentPage.totalElements,
                commentPage.totalPages,
                commentPage.first,
                commentPage.last
        )
    }

    @Override
    List<CommentDto> getCommentsByUserId(String userId) {
        return commentRepository.findAllByUserIdAndIsDeletedFalse(userId).stream()
                .map {commentMapper.toDto(it)}
                .collect(Collectors.toList())
    }

    @Override
    @Cacheable(value = "comments", key = "'user_' + #userId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    PageResponseDto<CommentDto> getCommentsByUserId(String userId, Pageable pageable) {
        log.info("Comment service: getting comments by user id with pagination: userId=${userId}, page=${pageable.pageNumber}, size=${pageable.pageSize}")
        Page<Comment> commentPage = commentRepository.findAllByUserIdAndIsDeletedFalse(userId, pageable)
        List<CommentDto> content = commentPage.content.stream()
                .map {commentMapper.toDto(it)}
                .collect(Collectors.toList())
        
        return new PageResponseDto<>(
                content,
                commentPage.number,
                commentPage.size,
                commentPage.totalElements,
                commentPage.totalPages,
                commentPage.first,
                commentPage.last
        )
    }

    @Override
    @Cacheable(value = "comments", key = "#id")
    CommentDto getCommentById(String id) {
        return commentMapper.toDto(commentRepository.findByIdAndIsDeletedFalse(id)
        .orElseThrow(() -> new EntityNotFoundException("Comment not found")))
    }

    @Override
    @CacheEvict(value = ["comments", "posts"], key = "'post_' + #result.postId")
    void deleteComment(String id) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"))
        
        String currentUserId = SecurityUtil.getCurrentUserId()
        if (!comment.getUserId().equals(currentUserId)) {
            log.warn("Comment service: User ${currentUserId} attempted to delete comment ${id} owned by ${comment.getUserId()}")
            throw new koval.proxyseller.twitter.exception.ResourceOwnershipException("You can only delete your own comments")
        }
        
        log.info("Comment service: deleting comment, by id: $id")
        comment.isDeleted = true
        commentRepository.save(comment)
    }

    @Override
    @CacheEvict(value = ["comments", "posts"], key = "'post_' + #result.postId")
    CommentDto updateComment(String id, CommentUpdateRequestDto commentUpdateRequestDto) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"))
        
        String currentUserId = SecurityUtil.getCurrentUserId()
        if (!comment.getUserId().equals(currentUserId)) {
            log.warn("Comment service: User ${currentUserId} attempted to update comment ${id} owned by ${comment.getUserId()}")
            throw new koval.proxyseller.twitter.exception.ResourceOwnershipException("You can only update your own comments")
        }

        log.info("Comment service: updating comment, by id: $id, request: $commentUpdateRequestDto")
        comment.setContent(commentUpdateRequestDto.getContent())
        comment.setUpdatedAt(java.time.Instant.now())
        CommentDto result = commentMapper.toDto(commentRepository.save(comment))

        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found"))

        log.info("Comment service: updating post, by id: ${post.getId()}, request: $commentUpdateRequestDto")
        post.getComments().stream()
                .filter {it.getId().equals(id)}
                .findFirst()
                .ifPresent {it.setContent(commentUpdateRequestDto.getContent())}
        postService.updatePost(postMapper.toDto(post))
        return result
    }
}
