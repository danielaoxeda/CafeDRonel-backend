package com.cafedronel.cafedronelbackend.services.password;

import com.cafedronel.cafedronelbackend.data.dto.password.ForgotRequest;
import com.cafedronel.cafedronelbackend.data.dto.password.ResetPasswordRequest;
import com.cafedronel.cafedronelbackend.data.model.Usuario;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.repository.UsuarioRepository;
import com.cafedronel.cafedronelbackend.services.email.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ImpPasswordService implements PasswordService {


    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public ImpPasswordService(UsuarioRepository usuarioRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public Boolean forgotAccount(ForgotRequest forgotRequest) {
        Usuario userFound = this.usuarioRepository.getUsuariosByCorreo(forgotRequest.email()).orElseThrow(
                () -> new BusinessException("El usuario no existe")
        );

        String recoveryCode = String.format("%06d", new Random().nextInt(999999));

        userFound.setRecoveryCode(recoveryCode);
        this.emailService.sendPasswordResetEmail(userFound.getCorreo(), recoveryCode);
        this.usuarioRepository.save(userFound);
        return true;
    }

    @Override
    public Boolean resetPassword(ResetPasswordRequest resetPasswordRequest) {
        Usuario userFound = this.usuarioRepository.findByCorreo(resetPasswordRequest.email()).orElseThrow(() -> new BusinessException("El usuario no existe"));

        if (userFound.getRecoveryCode() == null || resetPasswordRequest.recoveryCode() == null || !userFound.getRecoveryCode().equals(resetPasswordRequest.recoveryCode())) {
            throw new BusinessException("El código de recuperación no es valido");
        }
        
        userFound.setContrasena(passwordEncoder.encode(resetPasswordRequest.newPassword()));
        this.usuarioRepository.save(userFound);
        return true;

    }

}
