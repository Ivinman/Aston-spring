package ru.aston.module4.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.aston.module4.dto.UserDto;
import ru.aston.module4.dto.UserModel;
import ru.aston.module4.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	UserDto toDto(User user);
	User toUser(UserDto userDto);
	@Mapping(target = "id", ignore = true)
	void updateUserFromDto(UserModel dto, @MappingTarget User user);
}
