package koval.proxyseller.twitter.service.comment

import koval.proxyseller.twitter.dto.comment.CommentCreateRequestDto
import koval.proxyseller.twitter.dto.comment.CommentDto
import koval.proxyseller.twitter.dto.comment.CommentUpdateRequestDto

interface CommentService {
    /**
     * Method to create a comment
     * @param commentCreateRequestDto
     * @return CommentDto
     */
    CommentDto createComment(CommentCreateRequestDto commentCreateRequestDto)

    /**
     * Method to get all comments by post id
     * @return List<CommentDto>
     * @param postId
     */
    List<CommentDto> getCommentsByPostId(String postId)

    /**
     * Method to get all comments by post id with pagination
     * @param postId
     * @param pageable
     * @return PageResponseDto<CommentDto>
     */
    koval.proxyseller.twitter.dto.PageResponseDto<koval.proxyseller.twitter.dto.comment.CommentDto> getCommentsByPostId(String postId, org.springframework.data.domain.Pageable pageable)

    /**
     * Method to get all comments by user id
     * @return List<CommentDto>
     * @param userId
     */
    List<CommentDto> getCommentsByUserId(String userId)

    /**
     * Method to get all comments by user id with pagination
     * @param userId
     * @param pageable
     * @return PageResponseDto<CommentDto>
     */
    koval.proxyseller.twitter.dto.PageResponseDto<koval.proxyseller.twitter.dto.comment.CommentDto> getCommentsByUserId(String userId, org.springframework.data.domain.Pageable pageable)

    /**
     * Method to get comment by id
     * @param id
     * @return CommentDto
     */
    CommentDto getCommentById(String id)


    /**
     * Method to delete a comment by id
     * @param id
     */
    void deleteComment(String id)

    /**
     * Method to update a comment by id
     * @param id
     * @param commentUpdateRequestDto
     * @return CommentDto
     */
    CommentDto updateComment(String id, CommentUpdateRequestDto commentUpdateRequestDto)
}