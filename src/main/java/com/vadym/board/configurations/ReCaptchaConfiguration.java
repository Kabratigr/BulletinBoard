package com.vadym.board.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class ReCaptchaConfiguration {

    @Value("${google.recaptcha.key.site}")
    private String siteKey;

    @Value("${google.recaptcha.key.secret}")
    private String secretKey;

    private final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";
}
