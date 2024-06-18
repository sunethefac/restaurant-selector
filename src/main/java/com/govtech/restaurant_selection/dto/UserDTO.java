package com.govtech.restaurant_selection.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long userId;
    @NotEmpty(message = "Username should not be empty")
    private String username;
    @NotEmpty(message = "First Name should not be empty")
    private String firstName;
    @NotEmpty(message = "Last Name should not be empty")
    private String lastName;
    @NotEmpty(message = "Password should not be empty")
    private String password;

}
