package dev.vicestupinan.taskflow.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.vicestupinan.taskflow.security.JwtService;
import dev.vicestupinan.taskflow.user.Role;
import dev.vicestupinan.taskflow.user.User;
import dev.vicestupinan.taskflow.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticacionService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse login(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());
        String jwt = jwtService.generateToken(user);

        return new AuthenticationResponse(jwt);
    }

    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String jwt = jwtService.generateToken(user);

        return new AuthenticationResponse(jwt);
    }
}
