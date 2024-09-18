package com.staccato.memory.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.Objects;

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
        validateTermDates(startAt, endAt);
        this.startAt = startAt;
        this.endAt = endAt;
    }

    private void validateTermDates(LocalDate startAt, LocalDate endAt) {
        if (isOnlyOneDatePresent(startAt, endAt)) {
            throw new StaccatoException("추억의 시작 날짜와 끝 날짜는 함께 입력되거나, 함께 비워져 있어야 합니다.");
        }
        if (isInvalidTerm(startAt, endAt)) {
            throw new StaccatoException("끝 날짜가 시작 날짜보다 앞설 수 없어요.");
        }
    }

    private boolean isOnlyOneDatePresent(LocalDate startAt, LocalDate endAt) {
        return (Objects.nonNull(startAt) && Objects.isNull(endAt)) || (Objects.isNull(startAt) && Objects.nonNull(endAt));
    }

    private boolean isInvalidTerm(LocalDate startAt, LocalDate endAt) {
        return isExist(startAt, endAt) && endAt.isBefore(startAt);
    }

    public boolean doesNotContain(LocalDateTime date) {
        if (isExist(startAt, endAt)) {
            ChronoLocalDate targetDate = ChronoLocalDate.from(date);
            return (startAt.isAfter(targetDate) || endAt.isBefore(targetDate));
        }
        return false;
    }

    private boolean isExist(LocalDate startAt, LocalDate endAt) {
        return Objects.nonNull(startAt) && Objects.nonNull(endAt);
    }
}
