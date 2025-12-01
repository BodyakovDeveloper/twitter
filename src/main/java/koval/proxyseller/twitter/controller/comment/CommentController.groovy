package koval.proxyseller.twitter.controller.comment

import groovy.util.logging.Slf4j
import koval.proxyseller.twitter.dto.PageResponseDto
import koval.proxyseller.twitter.dto.comment.CommentCreateRequestDto
import koval.proxyseller.twitter.dto.comment.CommentDto
import koval.proxyseller.twitter.dto.comment.CommentUpdateRequestDto
import koval.proxyseller.twitter.service.comment.CommentService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
class CommentController implements CommentControllerApi {
    private final CommentService commentService

    CommentController(CommentService commentService) {
        this.commentService = commentService
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<CommentDto> createComment(CommentCreateRequestDto commentCreateRequestDto) {
        log.info("Comment controller: creating comment")
        CommentDto commentDto = commentService.createComment(commentCreateRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto)
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<List<CommentDto>> getCommentsByPostId(String id) {
        log.info("Comment controller: getting comments by post id: $id")
        return ResponseEntity.ok(commentService.getCommentsByPostId(id))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<PageResponseDto<CommentDto>> getCommentsByPostIdPaginated(String id, int page, int size, String sort) {
        log.info("Comment controller: getting comments by post id with pagination: postId=${id}, page=${page}, size=${size}, sort=${sort}")
        String[] sortParams = sort.split(",")
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]))
        return ResponseEntity.ok(commentService.getCommentsByPostId(id, pageable))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<List<CommentDto>> getCommentsByUserId(String id) {
        log.info("Comment controller: getting comments by user id: $id")
        return ResponseEntity.ok(commentService.getCommentsByUserId(id))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<PageResponseDto<CommentDto>> getCommentsByUserIdPaginated(String id, int page, int size, String sort) {
        log.info("Comment controller: getting comments by user id with pagination: userId=${id}, page=${page}, size=${size}, sort=${sort}")
        String[] sortParams = sort.split(",")
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]))
        return ResponseEntity.ok(commentService.getCommentsByUserId(id, pageable))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<CommentDto> getCommentById(String id) {
        log.info("Comment controller: getting comment by id: $id")
        return ResponseEntity.ok(commentService.getCommentById(id))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<CommentDto> updateComment(String id, CommentUpdateRequestDto commentUpdateRequestDto) {
        log.info("Comment controller: updating comment by id: $id")
        return ResponseEntity.ok(commentService.updateComment(id, commentUpdateRequestDto))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> deleteComment(String id) {
        log.info("Comment controller: deleting comment by id: $id")
        commentService.deleteComment(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
