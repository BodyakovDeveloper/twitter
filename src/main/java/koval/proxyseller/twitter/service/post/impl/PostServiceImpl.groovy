package koval.proxyseller.twitter.service.post.impl

import koval.proxyseller.twitter.dto.PageResponseDto
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
import koval.proxyseller.twitter.service.post.PostService
import groovy.util.logging.Slf4j
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

import java.util.stream.Collectors

@Slf4j
@Service
class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;

    PostServiceImpl(PostRepository postRepository, PostMapper postMapper, UserRepository userRepository) {
        this.postRepository = postRepository
        this.postMapper = postMapper

        this.userRepository = userRepository
    }

    @Override
    @CacheEvict(value = ["userPosts", "followingPosts"], allEntries = true)
    PostDto createPost(PostCreateRequestDto createPostRequestDto) {
        log.info("PostServiceImpl: Creating post: ${createPostRequestDto}")
        Post post = postMapper.toModel(createPostRequestDto)
        PostDto result = postMapper.toDto(postRepository.insert(post))
        return result
    }

    @Override
    @Cacheable(value = "posts", key = "#id")
    PostDto getPostById(String id) {
        log.info("PostServiceImpl: Getting post by id: ${id}")
        return postMapper.toDto(postRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found!")))
    }

    @Override
    @Deprecated
    List<PostDto> getAllPosts() {
        log.warn("PostServiceImpl: getAllPosts() without pagination is deprecated. Use getAllPosts(Pageable) instead.")
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 100)
        return getAllPosts(pageable).getContent()
    }

    @Override
    @Cacheable(value = "posts", key = "'all_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    PageResponseDto<PostDto> getAllPosts(Pageable pageable) {
        log.info("PostServiceImpl: Getting all posts with pagination: page=${pageable.pageNumber}, size=${pageable.pageSize}")
        Page<Post> postPage = postRepository.findAllByIsDeletedFalse(pageable)
        List<PostDto> content = postPage.content.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList())
        
        return new PageResponseDto<>(
                content,
                postPage.number,
                postPage.size,
                postPage.totalElements,
                postPage.totalPages,
                postPage.first,
                postPage.last
        )
    }

    @Override
    List<PostDto> getAllPostsByCurrentUser() {
        String userId = SecurityUtil.getCurrentUserId()
        log.info("PostServiceImpl: Getting all posts by current user: ${userId}")
        return postRepository.findAllByUserIdAndIsDeletedFalse(userId).stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList())
    }

    @Override
    @Cacheable(value = "userPosts", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    PageResponseDto<PostDto> getAllPostsByCurrentUser(Pageable pageable) {
        String userId = SecurityUtil.getCurrentUserId()
        log.info("PostServiceImpl: Getting all posts by current user with pagination: userId=${userId}, page=${pageable.pageNumber}, size=${pageable.pageSize}")
        Page<Post> postPage = postRepository.findAllByUserIdAndIsDeletedFalse(userId, pageable)
        List<PostDto> content = postPage.content.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList())
        
        return new PageResponseDto<>(
                content,
                postPage.number,
                postPage.size,
                postPage.totalElements,
                postPage.totalPages,
                postPage.first,
                postPage.last
        )
    }

    @Override
    @CacheEvict(value = ["posts", "userPosts", "followingPosts"], key = "#postDto.id")
    PostDto updatePost(PostDto postDto) {
        log.info("PostServiceImpl: Updating post: ${postDto}")
        Post post = postMapper.toModel(postDto)
        post.setUpdatedAt(java.time.Instant.now())
        PostDto updated = postMapper.toDto(postRepository.save(post))
        return updated
    }

    @Override
    @CacheEvict(value = ["posts", "userPosts", "followingPosts"], key = "#id")
    PostDto updatePost(String id, PostUpdateRequestDto updatePostRequestDto) {
        Post post = postRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found!"))
        
        String currentUserId = SecurityUtil.getCurrentUserId()
        if (!post.getUserId().equals(currentUserId)) {
            log.warn("PostServiceImpl: User ${currentUserId} attempted to update post ${id} owned by ${post.getUserId()}")
            throw new koval.proxyseller.twitter.exception.ResourceOwnershipException("You can only update your own posts")
        }
        
        post.setLocation(updatePostRequestDto.getLocation())
        post.setImageUrl(updatePostRequestDto.getImageUrl())
        post.setContent(updatePostRequestDto.getContent())
        post.setUpdatedAt(java.time.Instant.now())

        log.info("PostServiceImpl: Updating post by id: ${id}, ${updatePostRequestDto}")
        return postMapper.toDto(postRepository.save(post))
    }

    @Override
    @CacheEvict(value = ["posts", "userPosts", "followingPosts"], key = "#id")
    void deletePostById(String id) {
        Post post = postRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found!"))
        
        String currentUserId = SecurityUtil.getCurrentUserId()
        
        if (!post.getUserId().equals(currentUserId)) {
            log.warn("PostServiceImpl: User ${currentUserId} attempted to delete post ${id} owned by ${post.getUserId()}")
            throw new ResourceOwnershipException("You can only delete your own posts")
        }
        
        post.isDeleted = true

        log.info("PostServiceImpl: Deleting post by id: ${id}")
        postRepository.save(post)
    }

    @Override
    List<PostDto> getAllPostsByUserId(String id) {
        log.info("PostServiceImpl: Getting all posts by user id: ${id}")

        return postRepository.findAllByUserIdAndIsDeletedFalse(id).stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList())
    }

    @Override
    @Cacheable(value = "userPosts", key = "#id + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    PageResponseDto<PostDto> getAllPostsByUserId(String id, Pageable pageable) {
        log.info("PostServiceImpl: Getting all posts by user id with pagination: userId=${id}, page=${pageable.pageNumber}, size=${pageable.pageSize}")
        Page<Post> postPage = postRepository.findAllByUserIdAndIsDeletedFalse(id, pageable)
        List<PostDto> content = postPage.content.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList())
        
        return new PageResponseDto<>(
                content,
                postPage.number,
                postPage.size,
                postPage.totalElements,
                postPage.totalPages,
                postPage.first,
                postPage.last
        )
    }

    @Override
    @Deprecated
    List<PostDto> getAllPostsByFollowingUsers() {
        log.warn("PostServiceImpl: getAllPostsByFollowingUsers() without pagination is deprecated. Use getAllPostsByFollowingUsers(Pageable) instead.")
        Pageable pageable = PageRequest.of(0, 100)
        return getAllPostsByFollowingUsers(pageable).getContent()
    }

    @Override
    @Cacheable(value = "followingPosts", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    PageResponseDto<PostDto> getAllPostsByFollowingUsers(Pageable pageable) {
        String currentUserId = SecurityUtil.getCurrentUserId()
        User currentUser = userRepository.findByIdAndIsDeletedFalse(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found!"))

        log.info("PostServiceImpl: Getting all posts by following users with pagination: page=${pageable.pageNumber}, size=${pageable.pageSize}")
        Page<Post> postPage = postRepository.findAllByUserIdInAndIsDeletedFalse(currentUser.getFollowing(), pageable)
        List<PostDto> content = postPage.content.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList())
        
        return new PageResponseDto<>(
                content,
                postPage.number,
                postPage.size,
                postPage.totalElements,
                postPage.totalPages,
                postPage.first,
                postPage.last
        )
    }

    @Override
    Post getPostModelById(String id) {
        log.info("PostServiceImpl: Getting post model by id: ${id}")
        return postRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found!"))
    }
}
