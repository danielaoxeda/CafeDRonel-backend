package com.cafedronel.cafedronelbackend.controller;

import com.cafedronel.cafedronelbackend.util.JwtUtil;
import com.cafedronel.cafedronelbackend.model.Usuario;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        String correo = loginData.get("correo");
        String contrasena = loginData.get("contrasena");

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(correo, contrasena));

            Usuario usuario = usuarioRepository.findByCorreo(correo).orElseThrow();
            String token = jwtUtil.generateToken(correo);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("usuario", usuario.getNombre());
            return response;

        } catch (AuthenticationException e) {
            throw new RuntimeException("Credenciales inválidas");
        }
    }

}
