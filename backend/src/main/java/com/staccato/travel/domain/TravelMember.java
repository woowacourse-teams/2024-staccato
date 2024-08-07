package com.staccato.travel.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.staccato.config.domain.BaseEntity;
import com.staccato.member.domain.Member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @Builder
    public TravelMember(@NonNull Member member, @NonNull Travel travel) {
        this.member = member;
        this.travel = travel;
        travel.addTravelMember(this);
    }

    public boolean isMember(Member member) {
        return this.member.getId().equals(member.getId());
    }
}
