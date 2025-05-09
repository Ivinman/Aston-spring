package ru.aston.module4.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserUpdateDto;
import ru.aston.module4.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
	UserDto toDto(User user);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	User toUser(UserDto userDto);

	@Mapping(target = "id", ignore = true)
	void updateUserFromDto(UserUpdateDto dto, @MappingTarget User user);

	List<UserDto> toDtoList(List<User> users);
}
