package com.samuel.bowling.util;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@NoArgsConstructor
public class ControllerHttpUtil {
    public static HttpHeaders getPlainTextHeader() {
        HttpHeaders plainTextHeaders = new HttpHeaders();
        plainTextHeaders.setContentType(MediaType.TEXT_PLAIN);
        return plainTextHeaders;
    }
    public static HttpHeaders getApplicationJsonHeader() {
        HttpHeaders plainTextHeaders = new HttpHeaders();
        plainTextHeaders.setContentType(MediaType.APPLICATION_JSON);
        return plainTextHeaders;
    }
}
