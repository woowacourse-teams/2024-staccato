package com.staccato.category.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Objects;
import com.staccato.exception.StaccatoException;
import com.staccato.staccato.domain.Staccato;

public record StaccatoCursor(
        long id,
        LocalDateTime visitedAt
) {
    private static final StaccatoCursor EMPTY = new StaccatoCursor(-1L, null);
    private static final String DELIMITER = "\\|";
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    private static final int ARGUMENTS_COUNT = 2;
    private static final int ID_INDEX = 0;
    private static final int VISITED_AT_INDEX = 1;

    public StaccatoCursor {
        visitedAt = visitedAt == null ? null : LocalDateTime.parse(visitedAt.format(DATETIME_FORMAT), DATETIME_FORMAT);
    }

    public StaccatoCursor(Staccato staccato) {
        this(staccato.getId(), staccato.getVisitedAt());
    }

    public static StaccatoCursor fromEncoded(String encodedCursor) {
        if (Objects.isNull(encodedCursor) || encodedCursor.isBlank()) {
            return StaccatoCursor.empty();
        }
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(encodedCursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split(DELIMITER);
            if (parts.length != ARGUMENTS_COUNT) {
                throw new StaccatoException("주어진 커서 포멧(id|visitedAt)이 올바르지 않아요: " + encodedCursor);
            }

            long id = Long.parseLong(parts[ID_INDEX]);
            LocalDateTime visitedAt = LocalDateTime.parse(parts[VISITED_AT_INDEX], DATETIME_FORMAT);

            return new StaccatoCursor(id, visitedAt);
        } catch (Exception e) {
            throw new StaccatoException("주어진 커서 포멧(id|visitedAt)이 올바르지 않아요: " + encodedCursor, e);
        }
    }

    public static StaccatoCursor empty() {
        return EMPTY;
    }

    public boolean isEmpty() {
        return this.equals(EMPTY);
    }

    public String encode() {
        if (this.isEmpty()) {
            return null;
        }
        String cursor = String.format("%d|%s", id, visitedAt.format(DATETIME_FORMAT));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(cursor.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StaccatoCursor other)) {
            return false;
        }
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

}
