package koval.proxyseller.twitter.mapper.user

import koval.proxyseller.twitter.dto.user.UserDto
import koval.proxyseller.twitter.dto.user.UserUpdateRequestDto
import koval.proxyseller.twitter.model.user.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "password", expression = "java(koval.proxyseller.twitter.security.SecurityUtil.encodePassword(userUpdateRequestDto.getPassword()))")
    User toModel(UserUpdateRequestDto userUpdateRequestDto);

}