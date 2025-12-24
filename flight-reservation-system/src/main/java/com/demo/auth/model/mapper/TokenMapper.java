package com.demo.auth.model.mapper;

import com.demo.auth.model.RefreshToken;
import com.demo.auth.model.request.TokenRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    RefreshToken tokenRequestToRefreshToken(TokenRequest tokenRequest);
}
