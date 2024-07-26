package com.staccato.travel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.staccato.travel.domain.TravelMember;

public interface TravelMemberRepository extends JpaRepository<TravelMember, Long> {
    List<TravelMember> findAllByMemberIdAndIsDeletedIsFalseOrderByTravelStartAtDesc(long memberId);

    @Query("SELECT tm FROM TravelMember tm WHERE tm.member.id = :memberId AND YEAR(tm.travel.startAt) = :year AND tm.isDeleted IS FALSE ORDER BY tm.travel.startAt DESC")
    List<TravelMember> findAllByMemberIdAndStartAtYearDesc(@Param("memberId") long memberId, @Param("year") int year);
}
