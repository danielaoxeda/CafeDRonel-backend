package com.cafedronel.cafedronelbackend.services.password;

import com.cafedronel.cafedronelbackend.data.dto.password.ForgotRequest;
import com.cafedronel.cafedronelbackend.data.dto.password.ResetPasswordRequest;

public interface PasswordService {

    Boolean forgotAccount(ForgotRequest forgotRequest);
    Boolean resetPassword(ResetPasswordRequest resetPasswordRequest);
}
