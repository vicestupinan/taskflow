package dev.vicestupinan.taskflow.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String email;
    private String password;
    private String name;
}
