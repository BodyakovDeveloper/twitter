package koval.proxyseller.twitter.mapper.like

import koval.proxyseller.twitter.dto.like.LikeDto
import koval.proxyseller.twitter.dto.like.LikePostRequestDto
import koval.proxyseller.twitter.model.like.Like
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface LikeMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "userId", expression = "java(koval.proxyseller.twitter.security.SecurityUtil.getCurrentUserId())")
    Like toModel(LikePostRequestDto likeCreateRequestDto);

    Like toModel(LikeDto likeDto);

    LikeDto toDto(Like like);
}