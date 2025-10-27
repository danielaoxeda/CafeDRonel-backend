package com.cafedronel.cafedronelbackend.services.auth;

import com.cafedronel.cafedronelbackend.data.dto.auth.AuthResponse;
import com.cafedronel.cafedronelbackend.data.dto.auth.LoginRequest;
import com.cafedronel.cafedronelbackend.data.dto.auth.RegisterRequest;
import com.cafedronel.cafedronelbackend.data.dto.auth.VerifyRequest;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
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
                        loginRequest.correo(),
                        loginRequest.contrasena()
                )
        );

        Usuario userFound = usuarioRepository.findByCorreo(loginRequest.correo()).orElseThrow(() -> new BusinessException("El usuario no existe"));


        return new AuthResponse(jwtUtil.generateToken(authentication.getName()), authentication.getName(), authentication.getAuthorities().iterator().next().getAuthority(), userFound.getIdUsuario());
    }

    @Override
    public Boolean register(RegisterRequest registerRequest) {
        if (usuarioRepository.findByCorreo(registerRequest.correo()).isPresent()) {
            throw new BusinessException("El usuario ya esta registrado");
        }

        Usuario newUser = new Usuario();
        newUser.setCorreo(registerRequest.correo());
        newUser.setNombre(registerRequest.nombre());
        newUser.setContrasena(passwordEncoder.encode(registerRequest.contrasena()));
        newUser.setTelefono(registerRequest.telefono());
        newUser.setDireccion(registerRequest.direccion());
        newUser.setRol(registerRequest.rol());

        usuarioRepository.save(newUser);

        return true;
    }

    @Override
    public Boolean verify(VerifyRequest verifyRequest) {
        String token = verifyRequest.token();

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            String username = jwtUtil.extractEmail(token);

            if (jwtUtil.validateToken(token, username)) {
                return usuarioRepository.findByCorreo(username).isPresent();
            }
        } catch (Exception e) {
            throw new BusinessException("El token no es valido");
        }

        throw new BusinessException("El token no es valido");
    }
}
