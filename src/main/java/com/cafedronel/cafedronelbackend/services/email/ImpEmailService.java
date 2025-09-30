package com.cafedronel.cafedronelbackend.services.email;

import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class ImpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public ImpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String email, String resetCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Recuperación de Contraseña - CafeD'Ronnel");

            String htmlContent = buildEmailTemplate(resetCode);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new BusinessException("No se pudo enviar el email");
        }
    }

    private String buildEmailTemplate(String resetCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f9f9f9;
                    }
                    .header {
                        background-color: #6f4e37;
                        color: white;
                        padding: 20px;
                        text-align: center;
                    }
                    .content {
                        background-color: white;
                        padding: 30px;
                        border-radius: 5px;
                        margin-top: 20px;
                    }
                    .code-box {
                        background-color: #f0f0f0;
                        border: 2px solid #6f4e37;
                        border-radius: 5px;
                        padding: 20px;
                        text-align: center;
                        margin: 20px 0;
                    }
                    .code {
                        font-size: 32px;
                        font-weight: bold;
                        color: #6f4e37;
                        letter-spacing: 5px;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 20px;
                        color: #666;
                        font-size: 12px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Café D'Ronel</h1>
                    </div>
                    <div class="content">
                        <h2>Recuperación de Contraseña</h2>
                        <p>Has solicitado restablecer tu contraseña.</p>
                        <p>Utiliza el siguiente código para continuar con el proceso:</p>
                        
                        <div class="code-box">
                            <div class="code">%s</div>
                        </div>
                        
                        <p><strong>Este código solo se podrá usar una vez.</strong></p>
                        <p>Si no solicitaste este cambio, puedes ignorar este correo.</p>
                    </div>
                    <div class="footer">
                        <p>Este es un correo automático, por favor no responder.</p>
                        <p>&copy; 2025 Café D'Ronel. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(resetCode);
    }
}
