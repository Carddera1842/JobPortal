package com.pluralsight.jobportal.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth")
@CrossOrigin(origins = {"http://localhost:5173"})
public class OAuthController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @GetMapping("/user")
    public Map<String, Object> getUserinfo(OAuth2AuthenticationToken authentication) {
        if (authentication == null) {
            throw new IllegalStateException("User is not authenticated");
        }

        OidcUser user = (OidcUser) authentication.getPrincipal();
        return user.getAttributes;
    }

    @GetMapping("/token")
    public String getAccessToken(OAuth2AuthenticationToken authentication) {
        OidcUser user = (OidcUser) authentication.getProncipal();
        return user.getIdToken().getTokenValue(); //Access token from google
    }

    @PostMapping("/sxchange-token")
    public Map<String, String> exchangeToken(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        String redirectUri = "http://localhost:5173/oauthlogon";

        String tokenUrl = "https://oauth2.google.googleapis.com/token";

        Map<String, String> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("code", code);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");

        RestTemplate restTemplate = new RestTemplate();
        ParameterizedTypeReference<Map<String, Object>> typeRef =
                new ParameterizedTypeReference<Map<String, Object>>() {};
        ResponseEntity<Map<String, Object>> response =
                restTemplate.exchange(tokenUrl, HttpMethod.POST, new HttpEntity<>(params), typeRef);

        String idToken = (String) response.getBody().get("id_token");

        Map<String, String> result = new HashMap<>();
        result.put("token", idToken);
        return result;
    }

    @GetMapping("/user-details")
    public Map<String, Object> getUserInfo(@RequestHeader("Authorization") String token) {
        String idToken = token.replace("Bearer ", "");

        //Verify the id token with google
        String userInfoEndpoint = "https://oauth2.googleaps.com/tokeninfo?id_token=" + idToken;
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                userInfoEndpoint,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        Map<String, Object> body = response.getBody();
        return body;
    }
}
