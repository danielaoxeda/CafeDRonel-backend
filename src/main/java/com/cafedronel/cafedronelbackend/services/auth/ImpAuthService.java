package com.cafedronel.cafedronelbackend.services.auth;

import com.cafedronel.cafedronelbackend.data.dto.AuthResponse;
import com.cafedronel.cafedronelbackend.data.dto.LoginRequest;
import com.cafedronel.cafedronelbackend.data.dto.RegisterRequest;
import com.cafedronel.cafedronelbackend.data.dto.VerifyRequest;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import com.cafedronel.cafedronelbackend.util.jwt.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ImpAuthService implements AuthService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public ImpAuthService(JwtUtil jwtUtil, AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getCorreo(),
                        loginRequest.getContrasena()
                )
        );

        AuthResponse authResponse = new AuthResponse();

        authResponse.setEmail(authentication.getName());
        authResponse.setToken(jwtUtil.generateToken(authentication.getName()));
        authResponse.setRol(authentication.getAuthorities().iterator().next().getAuthority());

        System.out.println(authResponse);

        return authResponse;
    }

    @Override
    public Boolean register(RegisterRequest registerRequest) {
        if (usuarioRepository.findByCorreo(registerRequest.getCorreo()).isPresent()) {
            return false;
        }

        Usuario newUser = new Usuario();
        newUser.setCorreo(registerRequest.getCorreo());
        newUser.setNombre(registerRequest.getNombre());
        newUser.setContrasena(passwordEncoder.encode(registerRequest.getContrasena()));
        newUser.setTelefono(registerRequest.getTelefono());
        newUser.setDireccion(registerRequest.getDireccion());

        usuarioRepository.save(newUser);

        return true;
    }

    @Override
    public Boolean verify(VerifyRequest verifyRequest) {
        String token = verifyRequest.getToken();

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            String username = jwtUtil.extractEmail(token);

            if (jwtUtil.validateToken(token, username)) {
                return usuarioRepository.findByCorreo(username).isPresent();
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}
