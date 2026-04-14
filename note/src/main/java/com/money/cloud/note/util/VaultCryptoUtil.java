package com.money.cloud.note.util;

import com.money.cloud.common.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Component
public class VaultCryptoUtil {

    private final String secret;
    private SecretKeySpec keySpec;

    public VaultCryptoUtil(@Value("${security.vault.secret}") String secret) {
        this.secret = secret;
    }

    @PostConstruct
    public void init() {
        try {
            byte[] key = MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
            keySpec = new SecretKeySpec(Arrays.copyOf(key, 16), "AES");
        } catch (Exception ex) {
            throw new IllegalStateException("初始化保险箱加密失败", ex);
        }
    }

    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new BusinessException(500, "密码加密失败");
        }
    }

    public String decrypt(String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new BusinessException(500, "密码解密失败");
        }
    }
}
