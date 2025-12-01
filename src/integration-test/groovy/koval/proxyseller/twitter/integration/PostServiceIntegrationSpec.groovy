package koval.proxyseller.twitter.integration

import koval.proxyseller.twitter.dto.post.PostCreateRequestDto
import koval.proxyseller.twitter.dto.post.PostDto
import koval.proxyseller.twitter.dto.user.UserRegistrationRequestDto
import koval.proxyseller.twitter.service.post.PostService
import koval.proxyseller.twitter.service.user.UserService
import org.springframework.beans.factory.annotation.Autowired

class PostServiceIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    PostService postService

    @Autowired
    UserService userService

    def "should create a post"() {
        given: "a registered user and a post creation request"
        def userRequest = new UserRegistrationRequestDto(
                firstName: "Post",
                lastName: "Creator",
                username: "postcreator",
                email: "post.creator@example.com",
                password: "SecurePassword123!",
                age: 25
        )
        def user = userService.registerUser(userRequest)
        setupAuthenticatedUser("postcreator")

        def postRequest = new PostCreateRequestDto(
                content: "This is a test post",
                imageUrl: "https://example.com/image.jpg",
                location: "Test Location"
        )

        when: "creating the post"
        PostDto createdPost = postService.createPost(postRequest)

        then: "post should be created successfully"
        createdPost != null
        createdPost.id != null
        createdPost.content == "This is a test post"
        createdPost.userId != null
    }

    def "should get post by id"() {
        given: "a created post"
        def userRequest = new UserRegistrationRequestDto(
                firstName: "Get",
                lastName: "Post",
                username: "getpost",
                email: "get.post@example.com",
                password: "SecurePassword123!",
                age: 25
        )
        def user = userService.registerUser(userRequest)
        setupAuthenticatedUser("getpost")

        def postRequest = new PostCreateRequestDto(
                content: "Post to get",
                imageUrl: null,
                location: null
        )
        def createdPost = postService.createPost(postRequest)

        when: "getting post by id"
        def foundPost = postService.getPostById(createdPost.id)

        then: "post should be found"
        foundPost != null
        foundPost.id == createdPost.id
        foundPost.content == "Post to get"
    }

    def "should get all posts with pagination"() {
        given: "multiple posts"
        def userRequest = new UserRegistrationRequestDto(
                firstName: "Multi",
                lastName: "Post",
                username: "multipost",
                email: "multi.post@example.com",
                password: "SecurePassword123!",
                age: 25
        )
        def user = userService.registerUser(userRequest)
        setupAuthenticatedUser("multipost")

        (1..3).each { i ->
            def postRequest = new PostCreateRequestDto(
                    content: "Post $i",
                    imageUrl: null,
                    location: null
            )
            postService.createPost(postRequest)
        }

        when: "getting all posts with pagination"
        def pageable = org.springframework.data.domain.PageRequest.of(0, 10)
        def result = postService.getAllPosts(pageable)

        then: "posts should be returned"
        result != null
        result.content.size() >= 3
        result.totalElements >= 3
    }
}

