package com.cafedronel.cafedronelbackend.controllers.password;

import com.cafedronel.cafedronelbackend.data.dto.MessageResponse;
import com.cafedronel.cafedronelbackend.data.dto.password.ForgotRequest;
import com.cafedronel.cafedronelbackend.data.dto.password.ResetPasswordRequest;
import com.cafedronel.cafedronelbackend.services.password.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class PasswordController {


    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @PostMapping("/forgot")
    public ResponseEntity<MessageResponse<Boolean>> forgotPassword(@RequestBody ForgotRequest forgotRequest) {
        return ResponseEntity.ok(new MessageResponse<>(passwordService.forgotAccount(forgotRequest)));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse<Boolean>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        return ResponseEntity.ok(new MessageResponse<>(passwordService.resetPassword(resetPasswordRequest)));
    }
}
