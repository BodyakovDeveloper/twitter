package koval.proxyseller.twitter.controller.post

import groovy.util.logging.Slf4j
import koval.proxyseller.twitter.dto.PageResponseDto
import koval.proxyseller.twitter.dto.post.PostCreateRequestDto
import koval.proxyseller.twitter.dto.post.PostDto
import koval.proxyseller.twitter.dto.post.PostUpdateRequestDto
import koval.proxyseller.twitter.service.post.PostService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
class PostController implements PostControllerApi {
    private final PostService postService

    PostController(PostService postService) {
        this.postService = postService
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<PostDto> createPost(PostCreateRequestDto createPostRequestDto) {
        log.info("PostController: Creating post: ${createPostRequestDto}")
        PostDto postDto = postService.createPost(createPostRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(postDto)
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<PostDto> getPostById(String id) {
        log.info("PostController: Getting post by id: ${id}")
        return ResponseEntity.ok(postService.getPostById(id))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<List<PostDto>> getAllPosts() {
        log.info("PostController: Getting all posts")
        return ResponseEntity.ok(postService.getAllPosts())
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<PageResponseDto<PostDto>> getAllPostsPaginated(int page, int size, String sort) {
        log.info("PostController: Getting all posts with pagination: page=${page}, size=${size}, sort=${sort}")
        String[] sortParams = sort.split(",")
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]))
        return ResponseEntity.ok(postService.getAllPosts(pageable))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<List<PostDto>> getAllPostsByCurrentUser() {
        log.info("PostController: Getting all posts by current user")
        return ResponseEntity.ok(postService.getAllPostsByCurrentUser())
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<PageResponseDto<PostDto>> getAllPostsByCurrentUserPaginated(int page, int size, String sort) {
        log.info("PostController: Getting all posts by current user with pagination: page=${page}, size=${size}, sort=${sort}")
        String[] sortParams = sort.split(",")
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]))
        return ResponseEntity.ok(postService.getAllPostsByCurrentUser(pageable))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<List<PostDto>> getAllPostsByUserId(String id) {
        log.info("PostController: Getting all posts by user id: ${id}")
        return ResponseEntity.ok(postService.getAllPostsByUserId(id))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<PageResponseDto<PostDto>> getAllPostsByUserIdPaginated(String id, int page, int size, String sort) {
        log.info("PostController: Getting all posts by user id with pagination: userId=${id}, page=${page}, size=${size}, sort=${sort}")
        String[] sortParams = sort.split(",")
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]))
        return ResponseEntity.ok(postService.getAllPostsByUserId(id, pageable))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<List<PostDto>> getAllPostsByFollowingUsers() {
        log.info("PostController: Getting all posts by following users")
        return ResponseEntity.ok(postService.getAllPostsByFollowingUsers())
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<PageResponseDto<PostDto>> getAllPostsByFollowingUsersPaginated(int page, int size, String sort) {
        log.info("PostController: Getting all posts by following users with pagination: page=${page}, size=${size}, sort=${sort}")
        String[] sortParams = sort.split(",")
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]))
        return ResponseEntity.ok(postService.getAllPostsByFollowingUsers(pageable))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<PostDto> updatePost(String id, PostUpdateRequestDto postUpdateRequestDto) {
        log.info("PostController: Updating post by id: ${id}")
        return ResponseEntity.ok(postService.updatePost(id, postUpdateRequestDto))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> deletePostById(String id) {
        log.info("PostController: Deleting post by id: ${id}")
        postService.deletePostById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
