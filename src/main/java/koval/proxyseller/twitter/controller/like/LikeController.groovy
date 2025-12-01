package koval.proxyseller.twitter.controller.like

import groovy.util.logging.Slf4j
import koval.proxyseller.twitter.dto.like.LikeDto
import koval.proxyseller.twitter.dto.like.LikePostRequestDto
import koval.proxyseller.twitter.dto.like.UnlikePostRequestDto
import koval.proxyseller.twitter.service.like.LikeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@Slf4j
@RestController
class LikeController implements LikeControllerApi {
    private final LikeService likeService

    LikeController(LikeService likeService) {
        this.likeService = likeService
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<LikeDto> likePost(LikePostRequestDto likeCreateRequestDto) {
        log.info("Like controller: like post with id: ${likeCreateRequestDto.getPostId()}")
        LikeDto likeDto = likeService.likePost(likeCreateRequestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(likeDto)
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Void> unlikePost(UnlikePostRequestDto unlikePostRequestDto) {
        log.info("Like controller: unlike post with id: ${unlikePostRequestDto.getPostId()}")
        likeService.unlikePost(unlikePostRequestDto)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<Integer> getLikesCountByPostId(String id) {
        log.info("Like controller: get likes count by post id: $id")
        return ResponseEntity.ok(likeService.getLikesCount(id))
    }

    @Override
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<List<LikeDto>> getLikesByPostId(String id) {
        log.info("Like controller: get likes by post id: $id")
        return ResponseEntity.ok(likeService.getLikesByPostId(id))
    }
}
