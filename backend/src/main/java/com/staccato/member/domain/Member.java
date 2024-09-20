package com.staccato.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.staccato.config.domain.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET is_deleted = true WHERE id = ?")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SQLRestriction("is_deleted = false")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(nullable = false, unique = true)
    @Embedded
    private Nickname nickname;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
    private Boolean isDeleted = false;

    @Builder
    public Member(@NonNull String nickname, String imageUrl) {
        this.nickname = new Nickname(nickname);
        this.imageUrl = imageUrl;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isNotSame(Member member) {
        return !this.equals(member);
    }
}
