package com.staccato.visit.fixture;

import com.staccato.member.domain.Member;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitLog;

public class VisitLogFixture {
    public static VisitLog create(Visit visit, Member member) {
        return VisitLog.builder()
                .content("Sample Visit Log")
                .visit(visit)
                .member(member)
                .build();
    }
}
