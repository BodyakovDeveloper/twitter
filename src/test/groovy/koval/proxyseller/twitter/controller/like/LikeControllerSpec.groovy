package koval.proxyseller.twitter.controller.like

import com.fasterxml.jackson.databind.ObjectMapper
import koval.proxyseller.twitter.dto.like.LikeDto
import koval.proxyseller.twitter.dto.like.LikePostRequestDto
import koval.proxyseller.twitter.dto.like.UnlikePostRequestDto
import koval.proxyseller.twitter.service.like.LikeService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class LikeControllerSpec extends Specification {

    LikeService likeService = Mock()
    ObjectMapper objectMapper = new ObjectMapper()

    LikeController controller = new LikeController(likeService)
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build()

    def "should like a post"() {
        given: "a like request"
        def request = new LikePostRequestDto(postId: "post123")
        def likeDto = new LikeDto(id: "like123", postId: "post123", userId: "user123")

        when: "liking post"
        def result = mockMvc.perform(post("/api/v1/likes/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then: "like should be created"
        1 * likeService.likePost(_) >> likeDto
        result.andExpect(status().isCreated())
                .andExpect(jsonPath('$.id').value("like123"))
    }

    def "should unlike a post"() {
        given: "an unlike request"
        def request = new UnlikePostRequestDto(postId: "post123")

        when: "unliking post"
        def result = mockMvc.perform(post("/api/v1/likes/unlike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then: "like should be removed"
        1 * likeService.unlikePost(_)
        result.andExpect(status().isNoContent())
    }

    def "should get likes count by post id"() {
        given: "a post id"
        def postId = "post123"
        def count = 5

        when: "getting likes count"
        def result = mockMvc.perform(get("/api/v1/likes/post/count/{id}", postId))

        then: "count should be returned"
        1 * likeService.getLikesCount(postId) >> count
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$').value(5))
    }

    def "should get likes by post id"() {
        given: "a post id"
        def postId = "post123"
        def likes = [
                new LikeDto(id: "1", postId: postId),
                new LikeDto(id: "2", postId: postId)
        ]

        when: "getting likes"
        def result = mockMvc.perform(get("/api/v1/likes/post/{id}", postId))

        then: "likes should be returned"
        1 * likeService.getLikesByPostId(postId) >> likes
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$.length()').value(2))
    }
}

