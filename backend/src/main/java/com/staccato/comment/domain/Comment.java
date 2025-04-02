package com.staccato.comment.domain;

import com.staccato.staccato.domain.Staccato;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
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
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staccato_id", nullable = false)
    private Staccato staccato;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Comment(@NonNull String content, @NotNull Staccato staccato, @NonNull Member member) {
        this.content = content;
        this.staccato = staccato;
        this.member = member;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public boolean isNotOwnedBy(Member member) {
        return !Objects.equals(this.member, member);
    }
}
