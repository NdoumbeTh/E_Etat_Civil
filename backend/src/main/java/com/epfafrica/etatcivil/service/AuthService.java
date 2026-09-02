package com.epfafrica.etatcivil.service;

import com.epfafrica.etatcivil.dto.LoginRequest;
import com.epfafrica.etatcivil.dto.LoginResponse;
import com.epfafrica.etatcivil.dto.RegisterRequest;

public interface AuthService {

    /** @throws com.epfafrica.etatcivil.exception.EmailDejaUtiliseException si l'email est déjà pris */
    LoginResponse register(RegisterRequest request);

    /** @throws com.epfafrica.etatcivil.exception.IdentifiantsInvalidesException si email/mot de passe invalides */
    LoginResponse login(LoginRequest request);

    
}
