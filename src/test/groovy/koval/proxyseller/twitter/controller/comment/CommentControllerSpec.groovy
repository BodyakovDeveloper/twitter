package koval.proxyseller.twitter.controller.comment

import com.fasterxml.jackson.databind.ObjectMapper
import koval.proxyseller.twitter.dto.PageResponseDto
import koval.proxyseller.twitter.dto.comment.CommentCreateRequestDto
import koval.proxyseller.twitter.dto.comment.CommentDto
import koval.proxyseller.twitter.dto.comment.CommentUpdateRequestDto
import koval.proxyseller.twitter.service.comment.CommentService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class CommentControllerSpec extends Specification {

    CommentService commentService = Mock()
    ObjectMapper objectMapper = new ObjectMapper()

    CommentController controller = new CommentController(commentService)
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build()

    def "should create a comment"() {
        given: "a comment creation request"
        def request = new CommentCreateRequestDto(
                content: "Great post!",
                postId: "post123"
        )
        def commentDto = new CommentDto(id: "comment123", content: "Great post!", postId: "post123")

        when: "creating comment"
        def result = mockMvc.perform(post("/api/v1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then: "comment should be created"
        1 * commentService.createComment(_) >> commentDto
        result.andExpect(status().isCreated())
                .andExpect(jsonPath('$.id').value("comment123"))
    }

    def "should get comments by post id"() {
        given: "a post id"
        def postId = "post123"
        def comments = [
                new CommentDto(id: "1", postId: postId, content: "Comment 1"),
                new CommentDto(id: "2", postId: postId, content: "Comment 2")
        ]

        when: "getting comments by post id"
        def result = mockMvc.perform(get("/api/v1/comments/post/{id}", postId))

        then: "comments should be returned"
        1 * commentService.getCommentsByPostId(postId) >> comments
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$.length()').value(2))
    }

    def "should update comment"() {
        given: "a comment id and update request"
        def commentId = "comment123"
        def updateRequest = new CommentUpdateRequestDto(content: "Updated comment")
        def updatedComment = new CommentDto(id: commentId, content: "Updated comment")

        when: "updating comment"
        def result = mockMvc.perform(patch("/api/v1/comments/{id}", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))

        then: "comment should be updated"
        1 * commentService.updateComment(commentId, _) >> updatedComment
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.content').value("Updated comment"))
    }

    def "should delete comment"() {
        given: "a comment id"
        def commentId = "comment123"

        when: "deleting comment"
        def result = mockMvc.perform(delete("/api/v1/comments/{id}", commentId))

        then: "comment should be deleted"
        1 * commentService.deleteComment(commentId)
        result.andExpect(status().isNoContent())
    }
}

