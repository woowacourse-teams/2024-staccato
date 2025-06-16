package com.staccato.notification.domain;

import java.util.Arrays;
import com.staccato.exception.StaccatoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DeviceType {
    ANDROID("android"),
    IOS("ios");

    private String name;

    DeviceType(String name) {
        this.name = name;
    }

    public static DeviceType findByName(String deviceType) {
        return Arrays.stream(values())
                .filter(value -> value.name.equalsIgnoreCase(deviceType))
                .findFirst()
                .orElseThrow(() -> new StaccatoException("유효하지 않은 디바이스 타입입니다."));
    }
}
