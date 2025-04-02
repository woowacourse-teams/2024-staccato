package com.staccato.fixture.member;

import java.util.UUID;

import com.staccato.member.domain.Member;
import com.staccato.member.domain.Nickname;
import com.staccato.member.repository.MemberRepository;

public class MemberFixtures {

    public static MemberBuilder defaultMember() {
        return new MemberBuilder()
                .withNickname("nickname")
                .withImageUrl("https://example.com/memberImage.png")
                .withCode(UUID.randomUUID().toString());
    }

    public static class MemberBuilder {
        Long id;
        String nickname;
        String imageUrl;
        String code;

        public MemberBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public MemberBuilder withNickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public MemberBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public MemberBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public Member build() {
            return new Member(id, nickname, imageUrl, code);
        }

        public Member buildAndSave(MemberRepository repository) {
            Member member = build();
            return repository.save(member);
        }
    }
}
