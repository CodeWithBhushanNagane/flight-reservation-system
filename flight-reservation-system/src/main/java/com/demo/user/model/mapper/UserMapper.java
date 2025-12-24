package com.demo.user.model.mapper;

import com.demo.user.model.User;
import com.demo.user.model.request.RegisterRequest;
import com.demo.user.model.response.UpdatePasswordResponse;
import com.demo.user.model.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User registerRequestToUser(RegisterRequest registerRequest);

    UserResponse userToUserResponse(User user);
    UpdatePasswordResponse userToUpdatePasswordResponse(User user);
}
