package com.CourseCatlog.CourseCatlogApplication.service;

import com.CourseCatlog.CourseCatlogApplication.config.properties.ContentStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class ContentUrlService {

    private final ContentStorageProperties properties;

    public String signUrl(String rawUrl, long userId, long expiresAtEpochSeconds) {
        if (!StringUtils.hasText(rawUrl)) {
            return rawUrl;
        }
        String payload = rawUrl + "|" + userId + "|" + expiresAtEpochSeconds;
        String sig = hmacSha256(payload, properties.getSigningSecret());
        return rawUrl + (rawUrl.contains("?") ? "&" : "?") + "sig=" + sig + "&exp=" + expiresAtEpochSeconds + "&uid=" + userId;
    }

    private static String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(raw);
        } catch (Exception e) {
            throw new IllegalStateException("HMAC failed", e);
        }
    }
}
