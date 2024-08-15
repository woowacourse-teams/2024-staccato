package com.staccato.memory.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import com.staccato.config.domain.BaseEntity;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.moment.domain.Moment;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Memory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;
    @Column(nullable = false, length = 50)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private LocalDate startAt;
    private LocalDate endAt;
    @OneToMany(mappedBy = "memory", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<MemoryMember> memoryMembers = new ArrayList<>();

    @Builder
    public Memory(String thumbnailUrl, @NonNull String title, String description, LocalDate startAt, LocalDate endAt) {
        validateDate(startAt, endAt);
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    private void validateDate(LocalDate startAt, LocalDate endAt) {
        if (!(Objects.isNull(startAt) && Objects.isNull(endAt)) && endAt.isBefore(startAt)) {
            throw new StaccatoException("끝 날짜가 시작 날짜보다 앞설 수 없어요.");
        }
    }

    public void addMemoryMember(MemoryMember memoryMember) {
        memoryMembers.add(memoryMember);
    }

    public void addMemoryMember(Member member) {
        MemoryMember memoryMember = MemoryMember.builder()
                .memory(this)
                .member(member)
                .build();
        memoryMembers.add(memoryMember);
    }

    public void update(Memory updatedMemory, List<Moment> moments) {
        validateDuration(updatedMemory, moments);
        this.thumbnailUrl = updatedMemory.getThumbnailUrl();
        this.title = updatedMemory.getTitle();
        this.description = updatedMemory.getDescription();
        this.startAt = updatedMemory.getStartAt();
        this.endAt = updatedMemory.getEndAt();
    }

    private void validateDuration(Memory updatedMemory, List<Moment> moments) {
        moments.stream()
                .filter(moment -> updatedMemory.isWithoutDuration(moment.getVisitedAt()))
                .findAny()
                .ifPresent(moment -> {
                    throw new StaccatoException("변경하려는 추억 기간이 이미 존재하는 순간 기록을 포함하지 않습니다. 추억 기간을 다시 설정해주세요.");
                });
    }

    public boolean isWithoutDuration(LocalDateTime date) {
        if (Objects.isNull(startAt) && Objects.isNull(endAt)) {
            return false;
        }
        return startAt.isAfter(ChronoLocalDate.from(date)) || endAt.isBefore(ChronoLocalDate.from(date));
    }

    public List<Member> getMates() {
        return memoryMembers.stream()
                .map(MemoryMember::getMember)
                .toList();
    }

    public void assignThumbnail(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public boolean isNotOwnedBy(Member member) {
        return memoryMembers.stream()
                .noneMatch(memoryMember -> memoryMember.isMember(member));
    }
}
