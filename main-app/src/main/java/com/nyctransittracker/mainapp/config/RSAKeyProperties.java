package com.nyctransittracker.mainapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties("rsa")
public record RSAKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}
