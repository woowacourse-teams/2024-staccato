package com.staccato.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.hibernate.annotations.SQLDelete;

import com.staccato.config.domain.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET is_deleted = true WHERE id = ?")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @Embedded
    private Nickname nickname;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Builder
    public Member(@NonNull String nickname, String imageUrl) {
        this.nickname = new Nickname(nickname);
        this.imageUrl = imageUrl;
    }

    public String getNickname() {
        return nickname.getNickname();
    }
}
