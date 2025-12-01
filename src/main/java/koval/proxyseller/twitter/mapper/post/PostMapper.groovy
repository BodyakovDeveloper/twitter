package koval.proxyseller.twitter.mapper.post

import koval.proxyseller.twitter.dto.post.PostCreateRequestDto
import koval.proxyseller.twitter.dto.post.PostDto
import koval.proxyseller.twitter.dto.post.PostUpdateRequestDto
import koval.proxyseller.twitter.model.post.Post
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface PostMapper {
    PostDto toDto(Post post);

    Post toModel(PostDto postDto);

    @Mapping(target = "userId", expression = "java(koval.proxyseller.twitter.security.SecurityUtil.getCurrentUserId())")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    Post toModel(PostCreateRequestDto postCreateRequestDto);

    Post toModel(PostUpdateRequestDto postUpdateRequestDto);


}