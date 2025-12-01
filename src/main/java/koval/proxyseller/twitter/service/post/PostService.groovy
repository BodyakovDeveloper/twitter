package koval.proxyseller.twitter.service.post

import koval.proxyseller.twitter.dto.PageResponseDto
import koval.proxyseller.twitter.dto.post.PostCreateRequestDto
import koval.proxyseller.twitter.dto.post.PostDto
import koval.proxyseller.twitter.dto.post.PostUpdateRequestDto
import koval.proxyseller.twitter.model.post.Post
import org.springframework.data.domain.Pageable

/**
 * Interface to define the methods for the post service
 */
interface PostService {
    /**
     * Method to create a new post
     * @param createPostRequestDto
     * @return PostDto
     */
    PostDto createPost(PostCreateRequestDto createPostRequestDto);

    /**
     * Method to get a post by id
     * @param id
     * @return PostDto
     */
    PostDto getPostById(String id);

    /**
     * Method to get all posts
     * @return List<PostDto>
     */
    List<PostDto> getAllPosts();

    /**
     * Method to get all posts with pagination
     * @param pageable
     * @return PageResponseDto<PostDto>
     */
    PageResponseDto<PostDto> getAllPosts(Pageable pageable);

    /**
     * Method to get all posts by current user
     */
    List<PostDto> getAllPostsByCurrentUser();

    /**
     * Method to get all posts by current user with pagination
     * @param pageable
     * @return PageResponseDto<PostDto>
     */
    PageResponseDto<PostDto> getAllPostsByCurrentUser(Pageable pageable);

    /**
     * Method to update a post
     * @param id
     * @param updatePostRequestDto
     * @return PostDto
     */
    PostDto updatePost(String id, PostUpdateRequestDto updatePostRequestDto);

    /**
     * Method to update a post
     * @param postDto
     * @return PostDto
     */
    PostDto updatePost(PostDto postDto);

    /**
     * Method to delete a post by id
     * @param id
     */
    void deletePostById(String id);

    /**
     * Method to get all posts by user id
     * @param id
     * @return List<PostDto>
     */
    List<PostDto> getAllPostsByUserId(String id)

    /**
     * Method to get all posts by user id with pagination
     * @param id
     * @param pageable
     * @return PageResponseDto<PostDto>
     */
    PageResponseDto<PostDto> getAllPostsByUserId(String id, Pageable pageable)

    /**
     * Method to get all posts by following users
     * @return List<PostDto>
     */
    List<PostDto> getAllPostsByFollowingUsers()

    /**
     * Method to get all posts by following users with pagination
     * @param pageable
     * @return PageResponseDto<PostDto>
     */
    PageResponseDto<PostDto> getAllPostsByFollowingUsers(Pageable pageable)

    /**
     * Method to get post model by id
     * @param id
     */
    Post getPostModelById(String id)
}