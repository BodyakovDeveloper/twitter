package koval.proxyseller.twitter.service.like.impl

import koval.proxyseller.twitter.dto.like.LikeDto
import koval.proxyseller.twitter.dto.like.LikePostRequestDto
import koval.proxyseller.twitter.dto.like.UnlikePostRequestDto
import koval.proxyseller.twitter.exception.DuplicateEntityException
import koval.proxyseller.twitter.mapper.like.LikeMapper
import koval.proxyseller.twitter.mapper.post.PostMapper
import koval.proxyseller.twitter.model.like.Like
import koval.proxyseller.twitter.model.post.Post
import koval.proxyseller.twitter.exception.EntityNotFoundException
import koval.proxyseller.twitter.repository.like.LikeRepository
import koval.proxyseller.twitter.security.SecurityUtil
import koval.proxyseller.twitter.service.like.LikeService
import koval.proxyseller.twitter.service.post.PostService
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import java.util.stream.Collectors

@Slf4j
@Service
class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final LikeMapper likeMapper;
    private final PostMapper postMapper;

    LikeServiceImpl(LikeRepository likeRepository, PostService postService, LikeMapper likeMapper, PostMapper postMapper) {
        this.likeRepository = likeRepository
        this.postService = postService
        this.likeMapper = likeMapper
        this.postMapper = postMapper
    }

    @Override
    LikeDto likePost(LikePostRequestDto likeCreateRequestDto) {
        String currentUserId = SecurityUtil.getCurrentUserId()
        String postId = likeCreateRequestDto.getPostId()
        
        log.info("Like service: like post with id: ${postId}")
        
        if (likeRepository.existsByPostIdAndUserIdAndIsDeletedFalse(postId, currentUserId)) {
            log.warn("Like service: User ${currentUserId} already liked post ${postId}")
            throw new DuplicateEntityException("You have already liked this post")
        }
        
        Like like = likeMapper.toModel(likeCreateRequestDto)
        LikeDto likeDto = likeMapper.toDto(likeRepository.save(like))

        log.info("Like service: update post with id: ${postId}")
        Post post = postService.getPostModelById(postId)

        post.addLike(like)
        postService.updatePost(postMapper.toDto(post))
        return likeDto;
    }

    @Override
    void unlikePost(UnlikePostRequestDto unlikePostRequestDto) {
        String currentUserId = SecurityUtil.getCurrentUserId()
        String postId = unlikePostRequestDto.getPostId()
        
        log.info("Like service: unlike post with id: ${postId}")
        
        Like like = likeRepository.findByPostIdAndUserIdAndIsDeletedFalse(postId, currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Like not found"))
        
        like.isDeleted = true
        likeRepository.save(like)

        log.info("Like service: update post with id: ${postId}")
        Post post = postService.getPostModelById(postId)

        post.removeLike(like)
        postService.updatePost(postMapper.toDto(post))
    }

    @Override
    int getLikesCount(String id) {
        log.info("Like service: get likes count by post id: $id")
        return likeRepository.findByPostId(id).stream()
                .filter { !it.isDeleted }
                .count() as int
    }

    @Override
    List<LikeDto> getLikesByPostId(String id) {
        log.info("Like service: get likes by post id: $id")
        return postService.getPostModelById(id).getLikes().stream()
                .map { likeMapper.toDto(it) }
                .collect(Collectors.toList())
    }
}
