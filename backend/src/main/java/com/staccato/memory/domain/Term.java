package com.staccato.memory.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import com.staccato.exception.StaccatoException;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class Term {
    @Column
    private LocalDate startAt;
    @Column
    private LocalDate endAt;

    public Term(LocalDate startAt, LocalDate endAt) {
        validateDate(startAt, endAt);
        this.startAt = startAt;
        this.endAt = endAt;
    }

    private void validateDate(LocalDate startAt, LocalDate endAt) {
        if (endAt.isBefore(startAt)) {
            throw new StaccatoException("끝 날짜가 시작 날짜보다 앞설 수 없어요.");
        }
    }

    public boolean doesNotContain(LocalDateTime date) {
        return startAt.isAfter(ChronoLocalDate.from(date)) || endAt.isBefore(ChronoLocalDate.from(date));
    }
}
