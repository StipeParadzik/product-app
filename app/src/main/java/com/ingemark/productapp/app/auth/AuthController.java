package com.ingemark.productapp.app.auth;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ingemark.productapp.app.util.dto.DtoConverter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final DtoConverter<User, UserDto> dtoConverter;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void register(@Valid @RequestBody UserDto dto)
    {
        User user = dtoConverter.toPojo(dto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),
            request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow();
        String token = jwtUtil.generateToken(new UserDetailsImpl(user));

        return new LoginResponse(token);
    }
}

