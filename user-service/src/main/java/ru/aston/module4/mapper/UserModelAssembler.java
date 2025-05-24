package ru.aston.module4.mapper;

import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import ru.aston.module4.controller.UserController;
import ru.aston.module4.dto.UserDto;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler {

	public EntityModel<UserDto> toModel(UserDto user, RequestType context) {
		EntityModel<UserDto> model = EntityModel.of(user);

		switch (context) {
			case VIEW, CREATE -> {
				model.add(linkTo(methodOn(UserController.class).findUserById(user.getId())).withSelfRel());
				model.add(linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("update"));
				model.add(linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete"));
			}
			case UPDATE -> {
				model.add(linkTo(methodOn(UserController.class).findUserById(user.getId())).withSelfRel());
				model.add(linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete"));
			}
			case LIST ->
				model.add(linkTo(methodOn(UserController.class).findUserById(user.getId())).withSelfRel());
		}

		return model;
	}
}


