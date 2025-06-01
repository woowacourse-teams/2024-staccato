package com.staccato.comment.domain;

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
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.staccato.domain.Staccato;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Comment extends BaseEntity {
    private static final int MAX_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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
        validateLength(content);
        this.content = content;
        this.staccato = staccato;
        this.member = member;
    }

    public void changeContent(String content) {
        validateLength(content);
        this.content = content;
    }

    private void validateLength(String content) {
        if (content.isEmpty() || content.length() > MAX_LENGTH) {
            throw new StaccatoException("댓글은 공백 포함 500자 이하로 입력해주세요.");
        }
    }

    public boolean isNotOwnedBy(Member member) {
        return !Objects.equals(this.member, member);
    }
}
