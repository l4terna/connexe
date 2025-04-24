package com.laterna.connexeauth.v1._shared.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Configuration
@Slf4j
public class RandomConfig {
    @Bean
    public SecureRandom secureRandom() {
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstance("NativePRNG");

            byte[] seed = secureRandom.generateSeed(32);
            secureRandom.setSeed(seed);
        } catch (NoSuchAlgorithmException e) {
            log.warn("NativePRNG not available, falling back to default SecureRandom implementation", e);
            secureRandom = new SecureRandom();
        }
        return secureRandom;
    }
}
