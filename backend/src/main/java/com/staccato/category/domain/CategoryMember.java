package com.staccato.category.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.staccato.config.domain.BaseEntity;
import com.staccato.member.domain.Member;

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
@Table(name = "category_member",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"category_id", "member_id"})
        })
public class CategoryMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder
    public CategoryMember(@NonNull Member member, @NonNull Category category, @NonNull Role role) {
        this.member = member;
        this.category = category;
        this.role = role;
    }

    public boolean isOwnedBy(Member member) {
        return Objects.equals(this.member, member);
    }

    public boolean isGuest() {
        return role.isGuest();
    }
}
