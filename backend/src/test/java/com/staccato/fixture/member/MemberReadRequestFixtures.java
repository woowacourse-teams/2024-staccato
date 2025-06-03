package com.staccato.fixture.member;

import com.staccato.member.service.dto.request.MemberReadRequest;

public class MemberReadRequestFixtures {
    public static MemberReadRequestBuilder defaultMemberReadRequest() {
        return new MemberReadRequestBuilder()
                .withNickname("member");
    }

    public static class MemberReadRequestBuilder {
        private String nickname;
        private Long excludeCategoryId;

        public MemberReadRequestBuilder withNickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public MemberReadRequestBuilder withExcludeCategoryId(Long excludeCategoryId) {
            this.excludeCategoryId = excludeCategoryId;
            return this;
        }

        public MemberReadRequest build() {
            return new MemberReadRequest(nickname, excludeCategoryId);
        }
    }
}
