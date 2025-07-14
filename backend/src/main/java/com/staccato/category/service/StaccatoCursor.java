package com.staccato.category.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import com.staccato.exception.StaccatoException;

public record StaccatoCursor(
        long id,
        LocalDateTime visitedAt,
        LocalDateTime createdAt
) {
    private static final String DELIMITER = "\\|";
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final int ARGUMENTS_COUNT = 3;
    public static final int ID_INDEX = 0;
    public static final int VISITED_AT_INDEX = 1;
    public static final int CREATED_AT_INDEX = 2;

    public static StaccatoCursor fromEncoded(String encodedCursor) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(encodedCursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split(DELIMITER);
            if (parts.length != ARGUMENTS_COUNT) {
                throw new StaccatoException("주어진 커서 포멧(id|visitedAt|createdAt)이 올바르지 않아요: " + encodedCursor);
            }

            long id = Long.parseLong(parts[ID_INDEX]);
            LocalDateTime visitedAt = LocalDateTime.parse(parts[VISITED_AT_INDEX], DATETIME_FORMAT);
            LocalDateTime createdAt = LocalDateTime.parse(parts[CREATED_AT_INDEX], DATETIME_FORMAT);

            return new StaccatoCursor(id, visitedAt, createdAt);
        } catch (Exception e) {
            throw new StaccatoException("주어진 커서 포멧(id|visitedAt|createdAt)이 올바르지 않아요: " + encodedCursor, e);
        }
    }

    public String encode() {
        String cursor = String.format("%d|%s|%s",
                id,
                visitedAt.format(DATETIME_FORMAT),
                createdAt.format(DATETIME_FORMAT));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(cursor.getBytes(StandardCharsets.UTF_8));
    }
}
