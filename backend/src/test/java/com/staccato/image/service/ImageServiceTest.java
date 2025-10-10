package com.staccato.image.service;

import static org.assertj.core.api.Assertions.assertThat;

import static com.staccato.image.infrastructure.FakeS3Service.FAKE_CLOUD_FRONT_END_POINT;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.image.infrastructure.CloudStorageService;
import com.staccato.image.service.dto.DeletionResult;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.staccato.repository.StaccatoRepository;

class ImageServiceTest extends ServiceSliceTest {
    @Autowired
    private ImageService imageService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private StaccatoRepository staccatoRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CloudStorageService cloudStorageService;

    @DisplayName("사용되지 않는 S3 버킷 내 객체를 삭제합니다.")
    @Test
    void deleteUnusedImages() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withThumbnailUrl(FAKE_CLOUD_FRONT_END_POINT + "/" + "images/category.jpg")
                .withHost(member)
                .buildAndSave(categoryRepository);
        StaccatoFixtures.ofDefault(category).withStaccatoImages(
                        List.of(FAKE_CLOUD_FRONT_END_POINT + "/" + "images/staccato1-1.jpg",
                                FAKE_CLOUD_FRONT_END_POINT + "/" + "images/staccato1-2.jpg"))
                .buildAndSave(staccatoRepository);
        cloudStorageService.putS3Object("images/category.jpg", null, null);
        cloudStorageService.putS3Object("images/category2.jpg", null, null);
        cloudStorageService.putS3Object("images/staccato1-1.jpg", null, null);
        cloudStorageService.putS3Object("images/staccato1-2.jpg", null, null);
        cloudStorageService.putS3Object("images/staccato1-4.jpg", null, null);

        // when
        DeletionResult deleted = imageService.deleteUnusedImages();

        // then
        assertThat(deleted.successCount()).isEqualTo(2);
    }

}
