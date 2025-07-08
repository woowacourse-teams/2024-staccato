package com.staccato.invitation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.domain.InvitationStatus;
import com.staccato.member.domain.Member;

public interface CategoryInvitationRepository extends JpaRepository<CategoryInvitation, Long> {
    @Query("""
            SELECT ci
            from CategoryInvitation ci
            join fetch ci.category
            join fetch ci.invitee
            where ci.inviter.id = :inviterId
            and ci.status = 'REQUESTED'
            ORDER BY ci.createdAt DESC
            """)
    List<CategoryInvitation> findAllRequestedWithCategoryAndInviteeByInviterIdOrderByCreatedAtDesc(@Param("inviterId") long inviterId);

    boolean existsByCategoryIdAndInviterIdAndInviteeIdAndStatus(long categoryId, long inviterId, long inviteeId, InvitationStatus status);

    @Query("""
            SELECT ci
            from CategoryInvitation ci
            join fetch ci.category
            join fetch ci.inviter
            where ci.invitee.id = :inviteeId
            and ci.status = 'REQUESTED'
            ORDER BY ci.createdAt DESC
            """)
    List<CategoryInvitation> findAllRequestedWithCategoryAndInviterByInviteeIdOrderByCreatedAtDesc(@Param("inviteeId") long inviteeId);

    @Modifying
    @Query("DELETE FROM CategoryInvitation ci WHERE ci.category.id = :categoryId")
    void deleteAllByCategoryIdInBulk(long categoryId);

    List<CategoryInvitation> findAllByCategoryIdAndInviteeInAndStatus(long categoryId, List<Member> members, InvitationStatus invitationStatus);

    boolean existsByInviteeIdAndStatus(long inviteeId, InvitationStatus invitationStatus);
}
