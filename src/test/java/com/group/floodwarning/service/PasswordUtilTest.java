package com.group.floodwarning.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    @Test
    void hashAndMatches_validPassword_verifiesCorrectAndRejectsWrongPassword() {
        char[] raw = "StrongPass123!".toCharArray();
        String firstHash = passwordUtil.hash(raw);
        String secondHash = passwordUtil.hash("StrongPass123!".toCharArray());

        assertAll(
            () -> assertNotEquals(firstHash, secondHash),
            () -> assertTrue(passwordUtil.matches("StrongPass123!".toCharArray(), firstHash)),
            () -> assertFalse(passwordUtil.matches("WrongPass999!".toCharArray(), firstHash))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "invalid-format",
        "bcrypt$600000$c2FsdA==$aGFzaA==",
        "pbkdf2-sha256$notANumber$c2FsdA==$aGFzaA==",
        "pbkdf2-sha256$1000$c2FsdA==$aGFzaA==",
        "pbkdf2-sha256$600000$!!!invalid-base64!!!$aGFzaA=="
    })
    void matches_malformedOrWeakStoredHash_returnsFalse(String malformedHash) {
        assertFalse(passwordUtil.matches("StrongPass123!".toCharArray(), malformedHash));
    }
}
