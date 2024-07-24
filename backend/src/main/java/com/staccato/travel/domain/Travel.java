package com.staccato.travel.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import org.hibernate.annotations.SQLDelete;

import com.staccato.config.domain.BaseEntity;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.visit.domain.Visit;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE travel SET is_deleted = true WHERE id = ?")
public class Travel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;
    @Column(nullable = false, length = 50)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private LocalDate startAt;
    @Column(nullable = false)
    private LocalDate endAt;
    @OneToMany(mappedBy = "travel")
    private List<TravelMember> travelMembers = new ArrayList<>();

    @Builder
    public Travel(String thumbnailUrl, @NonNull String title, String description, @NonNull LocalDate startAt, @NonNull LocalDate endAt) {
        validateDate(startAt, endAt);
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    private void validateDate(LocalDate startAt, LocalDate endAt) {
        if (endAt.isBefore(startAt)) {
            throw new StaccatoException("끝 날짜가 시작 날짜보다 앞설 수 없어요.");
        }
    }

    public void addTravelMember(TravelMember travelMember) {
        travelMembers.add(travelMember);
    }

    public void update(Travel updatedTravel, List<Visit> visits) {
        validateDuration(updatedTravel, visits);
        this.thumbnailUrl = updatedTravel.getThumbnailUrl();
        this.title = updatedTravel.getTitle();
        this.description = updatedTravel.getDescription();
        this.startAt = updatedTravel.getStartAt();
        this.endAt = updatedTravel.getEndAt();
    }

    private void validateDuration(Travel updatedTravel, List<Visit> visits) {
        visits.stream()
                .filter(visit -> !updatedTravel.withinDuration(visit.getVisitedAt()))
                .findAny()
                .ifPresent(visit -> {
                    throw new StaccatoException("변경하려는 여행 기간이 이미 존재하는 방문 기록을 포함하지 않습니다. 여행 기간을 다시 설정해주세요.");
                });
    }

    public boolean withinDuration(LocalDate date) {
        return startAt.isBefore(date) && endAt.isAfter(date);
    }

    public List<Member> getMates() {
        return travelMembers.stream()
                .map(TravelMember::getMember)
                .toList();
    }
}
