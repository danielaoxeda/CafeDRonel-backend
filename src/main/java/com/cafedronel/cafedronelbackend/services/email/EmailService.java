package com.cafedronel.cafedronelbackend.services.email;

public interface EmailService {

    void sendPasswordResetEmail(String email, String resetCode);
}
