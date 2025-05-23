package com.staccato.invitation.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.staccato.invitation.domain.CategoryInvitation;
import com.staccato.invitation.domain.InvitationStatus;

public interface CategoryInvitationRepository extends JpaRepository<CategoryInvitation, Long> {
    @Query("""
            SELECT ci
            from CategoryInvitation ci
            join fetch ci.category
            join fetch ci.invitee
            where ci.inviter.id = :inviterId
            ORDER BY ci.createdAt DESC
            """)
    List<CategoryInvitation> findAllWithCategoryAndInviteeByInviterIdOrderByCreatedAtDesc(@Param("inviterId") long inviterId);

    boolean existsByCategoryIdAndInviterIdAndInviteeIdAndStatus(long categoryId, long inviterId, long inviteeId, InvitationStatus status);

    @Query("""
            SELECT ci
            from CategoryInvitation ci
            join fetch ci.category
            join fetch ci.inviter
            where ci.invitee.id = :inviteeId
            ORDER BY ci.createdAt DESC
            """)
    List<CategoryInvitation> findAllWithCategoryAndInviterByInviteeIdOrderByCreatedAtDesc(@Param("inviteeId") long inviteeId);
}
