package com.example.demo.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

@Service
public class SalesforceAuthService {

    @Value("${salesforce.clientId}")
    private String clientId;

    @Value("${salesforce.username}")
    private String username;

    @Value("${salesforce.loginUrl}")
    private String loginUrl;

    @Value("classpath:server.key")
    private Resource privateKeyFile;

    // Load private key
    private RSAPrivateKey loadPrivateKey() throws Exception {
        byte[] keyBytes = Files.readAllBytes(privateKeyFile.getFile().toPath());
        String keyString = new String(keyBytes)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = java.util.Base64.getDecoder().decode(keyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    // Create the JWT token
    public String generateJWT() throws Exception {
        RSAPrivateKey privateKey = loadPrivateKey();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(clientId)
                .subject(username)
                .audience(loginUrl)
                .expirationTime(new Date(System.currentTimeMillis() + 300000)) // 5 minutes
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        jwt.sign(new RSASSASigner(privateKey));

        return jwt.serialize();
    }
}
