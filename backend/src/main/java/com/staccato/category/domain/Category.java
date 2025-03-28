package com.staccato.category.domain;

import com.staccato.staccato.domain.Staccato;
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
import com.staccato.member.domain.Nickname;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {
    private static final String DEFAULT_SUBTITLE = "의 추억";
    private static final String DEFAULT_DESCRIPTION = "스타카토를 카테고리에 담아보세요.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;
    @Column(nullable = false, length = 50)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    private Color color;
    @Column
    @Embedded
    private Term term;
    @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST)
    private List<CategoryMember> categoryMembers = new ArrayList<>();

    @Builder
    public Category(String thumbnailUrl, @NonNull String title, String description, LocalDate startAt, LocalDate endAt) {
        this.thumbnailUrl = thumbnailUrl;
        this.title = title.trim();
        this.description = description;
        this.color = Color.GRAY;
        this.term = new Term(startAt, endAt);
    }

    public static Category basic(Nickname memberNickname) {
        return Category.builder()
                .title(memberNickname.getNickname() + DEFAULT_SUBTITLE)
                .description(DEFAULT_DESCRIPTION)
                .build();
    }

    public void addCategoryMember(Member member) {
        CategoryMember categoryMember = CategoryMember.builder()
                .category(this)
                .member(member)
                .build();
        categoryMembers.add(categoryMember);
    }

    public void update(Category updatedCategory, List<Staccato> staccatos) {
        validateDuration(updatedCategory, staccatos);
        this.thumbnailUrl = updatedCategory.getThumbnailUrl();
        this.title = updatedCategory.getTitle();
        this.description = updatedCategory.getDescription();
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

    public List<Member> getMates() {
        return categoryMembers.stream()
                .map(CategoryMember::getMember)
                .toList();
    }

    public boolean isNotOwnedBy(Member member) {
        return categoryMembers.stream()
                .noneMatch(categoryMember -> categoryMember.isMember(member));
    }

    public boolean isNotSameTitle(String title) {
        return !this.title.equals(title);
    }

    public boolean hasTerm() {
        return term.isExist();
    }

    public void changeColor(Color color){
        this.color = color;
    }
}
