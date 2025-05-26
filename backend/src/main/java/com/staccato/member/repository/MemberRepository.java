package com.staccato.member.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.staccato.invitation.domain.InvitationStatus;
import com.staccato.member.domain.Member;
import com.staccato.member.domain.Nickname;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByNickname(Nickname nickname);

    Optional<Member> findByCode(String code);

    @Query("""
                SELECT m FROM Member m
                WHERE m.nickname.nickname LIKE CONCAT('%', :nickname, '%')
                AND m.id <> :memberId
                AND (
                  :categoryId IS NULL OR (
                    m.id NOT IN (
                        SELECT ci.invitee.id FROM CategoryInvitation ci
                        WHERE ci.category.id = :categoryId AND ci.status = :status
                    )
                    AND m.id NOT IN (
                        SELECT cm.member.id FROM CategoryMember cm
                        WHERE cm.category.id = :categoryId
                    )
                  )
                )
            """)
    List<Member> findByNicknameNicknameContainsAndMemberIddNotAndCategoryNot(
            @Param("nickname") String nickname,
            @Param("memberId") long memberId,
            @Param("categoryId") Long categoryId,
            @Param("status") InvitationStatus status
    );

    List<Member> findAllByIdIn(Set<Long> memberIds);
}
