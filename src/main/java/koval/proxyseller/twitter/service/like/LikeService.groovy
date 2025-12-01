package koval.proxyseller.twitter.service.like

import koval.proxyseller.twitter.dto.like.LikeDto
import koval.proxyseller.twitter.dto.like.LikePostRequestDto
import koval.proxyseller.twitter.dto.like.UnlikePostRequestDto

/**
 * Interface to define the methods for like service
 */
interface LikeService {
    /**
     * Method to like a post by post id
     * @param id
     * @return LikeDto
     */
    LikeDto likePost(LikePostRequestDto likeCreateRequestDto);

    /**
     * Method to unlike a post by post id
     * @param id
     */
    void unlikePost(UnlikePostRequestDto unlikePostRequestDto);

    /**
     * Method to get the count of likes by post id
     * @param id
     * @return int
     */
    int getLikesCount(String id);

    /**
     * Method to get all post likes by post id
     * @param id
     * @return List<LikeDto>
     */
    List<LikeDto> getLikesByPostId(String id);
}
