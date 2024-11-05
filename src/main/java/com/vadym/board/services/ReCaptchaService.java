package com.vadym.board.services;

import com.vadym.board.configurations.ReCaptchaConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReCaptchaService {

    private final ReCaptchaConfiguration reCaptchaConfiguration;

    public boolean validateRecaptcha(String response) {
        String url = String.format(reCaptchaConfiguration.getRECAPTCHA_VERIFY_URL(), reCaptchaConfiguration.getSecretKey(), response);
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> request = new HashMap<>();
        request.put("response", response);
        Map<String, Object> result = restTemplate.postForObject(url, request, Map.class);
        return Boolean.TRUE.equals(result.get("success"));
    }
}
