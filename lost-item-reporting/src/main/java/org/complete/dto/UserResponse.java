package org.complete.dto;

import lombok.Getter;
import org.complete.domain.User;

@Getter
public class UserResponse {

    private final String email;
    private final String name;

    public UserResponse(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
    }
}
