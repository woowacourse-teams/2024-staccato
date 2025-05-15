package com.staccato.category.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
public class Category extends BaseEntity {
    private static final String DEFAULT_SUBTITLE = "의 추억";
    private static final String DEFAULT_DESCRIPTION = "스타카토를 카테고리에 담아보세요.";
    private static final String DEFAULT_COLOR = Color.GRAY.getName();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;
    @Column(nullable = false, length = 50)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Color color;
    @Column
    @Embedded
    private Term term;
    @Column(nullable = false)
    private Boolean isShared;
    @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST)
    private List<CategoryMember> categoryMembers = new ArrayList<>();

    public Category(String thumbnailUrl, @NonNull String title, String description, Color color, LocalDate startAt,
                    LocalDate endAt, @NonNull Boolean isShared) {
        this.thumbnailUrl = thumbnailUrl;
        this.title = title.trim();
        this.description = description;
        this.color = color;
        this.term = new Term(startAt, endAt);
        this.isShared = isShared;
    }

    @Builder
    public Category(String thumbnailUrl, @NonNull String title, String description, @NonNull String color,
                    LocalDate startAt, LocalDate endAt, @NonNull Boolean isShared) {
        this(thumbnailUrl, title, description, Color.findByName(color), startAt, endAt, isShared);
    }

    public static Category basic(Member member) {
        Category category = Category.builder()
                .title(member.getNickname().getNickname() + DEFAULT_SUBTITLE)
                .description(DEFAULT_DESCRIPTION)
                .color(DEFAULT_COLOR)
                .isShared(false)
                .build();

        category.addHost(member);
        return category;
    }

    public void addHost(Member member) {
        CategoryMember categoryMember = CategoryMember.builder()
                .category(this)
                .member(member)
                .role(Role.HOST)
                .build();
        categoryMembers.add(categoryMember);
    }

    public void addGuests(List<Member> members) {
        members.forEach(member -> {
            CategoryMember categoryMember = CategoryMember.builder()
                    .category(this)
                    .member(member)
                    .role(Role.GUEST)
                    .build();
            categoryMembers.add(categoryMember);
        });
    }

    public void update(Category updatedCategory, List<Staccato> staccatos) {
        validateDuration(updatedCategory, staccatos);
        this.thumbnailUrl = updatedCategory.getThumbnailUrl();
        this.title = updatedCategory.getTitle();
        this.description = updatedCategory.getDescription();
        this.color = updatedCategory.getColor();
        this.term = updatedCategory.getTerm();
    }

    private void validateDuration(Category updatedCategory, List<Staccato> staccatos) {
        staccatos.stream()
                .filter(staccato -> updatedCategory.isWithoutDuration(staccato.getVisitedAt()))
                .findAny()
                .ifPresent(staccato -> {
                    throw new StaccatoException("기간이 이미 존재하는 스타카토를 포함하지 않아요. 다시 설정해주세요.");
                });
    }

    public boolean isWithoutDuration(LocalDateTime date) {
        return term.doesNotContain(date);
    }

    public boolean isNotOwnedBy(Member member) {
        return categoryMembers.stream()
                .noneMatch(categoryMember -> categoryMember.isOwnedBy(member));
    }

    public boolean isGuest(Member member) {
        return categoryMembers.stream()
                .filter(categoryMember -> categoryMember.isOwnedBy(member))
                .findFirst()
                .map(CategoryMember::isGuest)
                .orElse(false);
    }

    public boolean isNotSameTitle(String title) {
        return !this.title.equals(title);
    }

    public boolean hasTerm() {
        return term.isExist();
    }

    public void changeColor(Color color) {
        this.color = color;
    }
}
