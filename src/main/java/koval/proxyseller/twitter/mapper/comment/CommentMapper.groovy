package koval.proxyseller.twitter.mapper.comment

import koval.proxyseller.twitter.dto.comment.CommentCreateRequestDto
import koval.proxyseller.twitter.dto.comment.CommentDto
import koval.proxyseller.twitter.model.comment.Comment
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface CommentMapper {
    Comment toModel(CommentDto commentDto);

    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "userId", expression = "java(koval.proxyseller.twitter.security.SecurityUtil.getCurrentUserId())")
    Comment toModel(CommentCreateRequestDto commentCreateRequestDto);

    CommentDto toDto(Comment comment);

}