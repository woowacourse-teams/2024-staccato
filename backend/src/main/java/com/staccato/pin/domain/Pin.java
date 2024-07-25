package com.staccato.pin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import org.hibernate.annotations.SQLDelete;

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
@SQLDelete(sql = "UPDATE pin SET is_deleted = true WHERE id = ?")
public class Pin extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String place;
    @Column(nullable = false)
    private String address;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public Pin(@NonNull String place, @NonNull String address, @NonNull Member member) {
        this.place = place;
        this.address = address;
        this.member = member;
    }
}
