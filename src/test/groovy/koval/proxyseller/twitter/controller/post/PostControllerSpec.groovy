package koval.proxyseller.twitter.controller.post

import com.fasterxml.jackson.databind.ObjectMapper
import koval.proxyseller.twitter.dto.PageResponseDto
import koval.proxyseller.twitter.dto.post.PostCreateRequestDto
import koval.proxyseller.twitter.dto.post.PostDto
import koval.proxyseller.twitter.dto.post.PostUpdateRequestDto
import koval.proxyseller.twitter.service.post.PostService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class PostControllerSpec extends Specification {

    PostService postService = Mock()
    ObjectMapper objectMapper = new ObjectMapper()

    PostController controller = new PostController(postService)
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build()

    def "should create a post"() {
        given: "a post creation request"
        def request = new PostCreateRequestDto(
                content: "Test post",
                imageUrl: "https://example.com/image.jpg",
                location: "Location"
        )
        def postDto = new PostDto(id: "post123", content: "Test post", userId: "user123")

        when: "creating post"
        def result = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then: "post should be created"
        1 * postService.createPost(_) >> postDto
        result.andExpect(status().isCreated())
                .andExpect(jsonPath('$.id').value("post123"))
    }

    def "should get post by id"() {
        given: "a post id"
        def postId = "post123"
        def postDto = new PostDto(id: postId, content: "Test post")

        when: "getting post by id"
        def result = mockMvc.perform(get("/api/v1/posts/{id}", postId))

        then: "post should be returned"
        1 * postService.getPostById(postId) >> postDto
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.id').value(postId))
    }

    def "should get all posts"() {
        given: "multiple posts"
        def posts = [
                new PostDto(id: "1", content: "Post 1"),
                new PostDto(id: "2", content: "Post 2")
        ]

        when: "getting all posts"
        def result = mockMvc.perform(get("/api/v1/posts"))

        then: "posts should be returned"
        1 * postService.getAllPosts() >> posts
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$').isArray())
    }

    def "should update post"() {
        given: "a post id and update request"
        def postId = "post123"
        def updateRequest = new PostUpdateRequestDto(
                content: "Updated content",
                imageUrl: "https://example.com/new.jpg",
                location: "New location"
        )
        def updatedPost = new PostDto(id: postId, content: "Updated content")

        when: "updating post"
        def result = mockMvc.perform(put("/api/v1/posts/{id}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))

        then: "post should be updated"
        1 * postService.updatePost(postId, _) >> updatedPost
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.content').value("Updated content"))
    }

    def "should delete post"() {
        given: "a post id"
        def postId = "post123"

        when: "deleting post"
        def result = mockMvc.perform(delete("/api/v1/posts/{id}", postId))

        then: "post should be deleted"
        1 * postService.deletePostById(postId)
        result.andExpect(status().isNoContent())
    }
}

