package koval.proxyseller.twitter.service.post


import koval.proxyseller.twitter.dto.post.PostCreateRequestDto
import koval.proxyseller.twitter.dto.post.PostDto
import koval.proxyseller.twitter.dto.post.PostUpdateRequestDto
import koval.proxyseller.twitter.exception.EntityNotFoundException
import koval.proxyseller.twitter.exception.ResourceOwnershipException
import koval.proxyseller.twitter.mapper.post.PostMapper
import koval.proxyseller.twitter.model.post.Post
import koval.proxyseller.twitter.model.user.User
import koval.proxyseller.twitter.repository.post.PostRepository
import koval.proxyseller.twitter.repository.user.UserRepository
import koval.proxyseller.twitter.security.SecurityUtil
import koval.proxyseller.twitter.service.post.impl.PostServiceImpl
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification
import spock.lang.Subject

class PostServiceImplSpec extends Specification {

    PostRepository postRepository = Mock()
    PostMapper postMapper = Mock()
    UserRepository userRepository = Mock()

    @Subject
    PostServiceImpl postService = new PostServiceImpl(postRepository, postMapper, userRepository)

    def "should create a post successfully"() {
        given: "a post creation request"
        def request = new PostCreateRequestDto(
                content: "Test post",
                imageUrl: "https://example.com/image.jpg",
                location: "Location"
        )
        def post = new Post(id: "post123", content: "Test post")
        def postDto = new PostDto(id: "post123", content: "Test post", userId: "user123")

        when: "creating the post"
        def result = postService.createPost(request)

        then: "post should be created"
        1 * postMapper.toModel(request) >> post
        1 * postRepository.insert(post) >> post
        1 * postMapper.toDto(post) >> postDto
        result != null
        result.id == "post123"
    }

    def "should get post by id"() {
        given: "a post id"
        def postId = "post123"
        def post = new Post(id: postId, content: "Test post")
        def postDto = new PostDto(id: postId, content: "Test post")

        when: "getting post by id"
        def result = postService.getPostById(postId)

        then: "post should be returned"
        1 * postRepository.findByIdAndIsDeletedFalse(postId) >> Optional.of(post)
        1 * postMapper.toDto(post) >> postDto
        result != null
        result.id == postId
    }

    def "should throw EntityNotFoundException when post not found"() {
        given: "a non-existent post id"
        def postId = "nonexistent"

        when: "getting post by id"
        postService.getPostById(postId)

        then: "should throw EntityNotFoundException"
        1 * postRepository.findByIdAndIsDeletedFalse(postId) >> Optional.empty()
        thrown(EntityNotFoundException)
    }

    def "should get all posts with pagination"() {
        given: "a pageable request"
        def pageable = PageRequest.of(0, 10)
        def posts = [new Post(id: "1"), new Post(id: "2")]
        def postDtos = [new PostDto(id: "1"), new PostDto(id: "2")]
        def page = new PageImpl<>(posts, pageable, 2)

        when: "getting all posts"
        def result = postService.getAllPosts(pageable)

        then: "paginated posts should be returned"
        1 * postRepository.findAllByIsDeletedFalse(pageable) >> page
        2 * postMapper.toDto(_) >> { Post p -> postDtos[posts.indexOf(p)] }
        result != null
        result.content.size() == 2
    }

    def "should update post successfully when user is owner"() {
        given: "a post id and update request"
        def postId = "post123"
        def userId = "user123"
        def post = new Post(id: postId, userId: userId, content: "Old content")
        def updateRequest = new PostUpdateRequestDto(
                content: "New content",
                imageUrl: "https://example.com/new.jpg",
                location: "New location"
        )
        def updatedPost = new Post(id: postId, content: "New content")
        def postDto = new PostDto(id: postId, content: "New content")

        when: "updating the post"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> userId }
        def result = postService.updatePost(postId, updateRequest)

        then: "post should be updated"
        1 * postRepository.findByIdAndIsDeletedFalse(postId) >> Optional.of(post)
        1 * postRepository.save(_) >> { Post p ->
            assert p.content == "New content"
            updatedPost
        }
        1 * postMapper.toDto(updatedPost) >> postDto
        result != null
    }

    def "should throw ResourceOwnershipException when user is not owner"() {
        given: "a post owned by another user"
        def postId = "post123"
        def post = new Post(id: postId, userId: "otherUser", content: "Content")
        def updateRequest = new PostUpdateRequestDto(content: "New content")
        def currentUserId = "currentUser"

        when: "updating the post"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> currentUserId }
        postService.updatePost(postId, updateRequest)

        then: "should throw ResourceOwnershipException"
        1 * postRepository.findByIdAndIsDeletedFalse(postId) >> Optional.of(post)
        thrown(ResourceOwnershipException)
    }

    def "should get all posts by current user"() {
        given: "current user id"
        def userId = "user123"
        def posts = [new Post(id: "1", userId: userId), new Post(id: "2", userId: userId)]
        def postDtos = [new PostDto(id: "1"), new PostDto(id: "2")]

        when: "getting posts by current user"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> userId }
        def result = postService.getAllPostsByCurrentUser()

        then: "user posts should be returned"
        1 * postRepository.findAllByUserIdAndIsDeletedFalse(userId) >> posts
        2 * postMapper.toDto(_) >> { Post p -> postDtos[posts.indexOf(p)] }
        result != null
        result.size() == 2
    }

    def "should get all posts by current user with pagination"() {
        given: "current user id and pageable"
        def userId = "user123"
        def pageable = PageRequest.of(0, 10)
        def posts = [new Post(id: "1", userId: userId)]
        def page = new PageImpl<>(posts, pageable, 1)

        when: "getting posts by current user"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> userId }
        def result = postService.getAllPostsByCurrentUser(pageable)

        then: "paginated user posts should be returned"
        1 * postRepository.findAllByUserIdAndIsDeletedFalse(userId, pageable) >> page
        1 * postMapper.toDto(_) >> new PostDto(id: "1")
        result != null
        result.content.size() == 1
    }

    def "should get all posts by user id"() {
        given: "a user id"
        def userId = "user123"
        def posts = [new Post(id: "1", userId: userId), new Post(id: "2", userId: userId)]
        def postDtos = [new PostDto(id: "1"), new PostDto(id: "2")]

        when: "getting posts by user id"
        def result = postService.getAllPostsByUserId(userId)

        then: "user posts should be returned"
        1 * postRepository.findAllByUserIdAndIsDeletedFalse(userId) >> posts
        2 * postMapper.toDto(_) >> { Post p -> postDtos[posts.indexOf(p)] }
        result != null
        result.size() == 2
    }

    def "should get all posts by following users"() {
        given: "current user following other users"
        def currentUserId = "user123"
        def currentUser = new User(id: currentUserId, following: ["user1", "user2"] as Set)
        def posts = [new Post(id: "1"), new Post(id: "2")]
        def postDtos = [new PostDto(id: "1"), new PostDto(id: "2")]
        def pageable = PageRequest.of(0, 100)
        def postPage = new PageImpl<>(posts, pageable, posts.size())

        when: "getting posts by following users"
        SecurityUtil.metaClass.static.getCurrentUserId = { -> currentUserId }
        def result = postService.getAllPostsByFollowingUsers()

        then: "following users posts should be returned"
        1 * userRepository.findByIdAndIsDeletedFalse(currentUserId) >> Optional.of(currentUser)
        1 * postRepository.findAllByUserIdInAndIsDeletedFalse(currentUser.following, pageable) >> postPage
        2 * postMapper.toDto(_) >> { Post p -> postDtos[posts.indexOf(p)] }
        result != null
        result.size() == 2
    }
}

