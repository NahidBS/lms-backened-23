package com.library.management.security;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory token blacklist to support logout functionality.
 * <p>
 * When a user logs out the JWT they were using is placed into this blacklist.
 * Incoming requests presenting a blacklisted token will not be authenticated.
 *
 * <p>
 * Note: In a production system you may want to persist these tokens in a
 * database or external cache with an expiry aligned to the JWT expiration.
 * This implementation keeps tokens in memory for the life of the application.
 */
@Service
public class TokenBlacklistService {

    /**
     * Thread-safe set to store blacklisted JWTs. We use a concurrent
     * set since it will be accessed by multiple threads (Spring filter
     * chain) concurrently.
     */
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    /**
     * Adds the provided JWT to the blacklist.
     *
     * @param token the raw JWT (without Bearer prefix) to blacklist
     */
    public void blacklistToken(String token) {
        if (token != null && !token.isBlank()) {
            blacklistedTokens.add(token);
        }
    }

    /**
     * Checks whether the provided token has been blacklisted via logout.
     *
     * @param token the raw JWT (without Bearer prefix)
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isBlacklisted(String token) {
        return token != null && blacklistedTokens.contains(token);
    }
}