package com.staccato.image.service;

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
import com.staccato.image.infrastructure.S3ObjectClient;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.staccato.repository.StaccatoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static com.staccato.image.infrastructure.FakeS3ObjectClient.FAKE_CLOUD_FRONT_END_POINT;

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
    private S3ObjectClient cloudStorageClient;

    @DisplayName("사용되지 않는 S3 버킷 내 객체를 삭제합니다.")
    @Test
    void deleteUnusedImages() {
        // given
        Member member = MemberFixtures.defaultMember().buildAndSave(memberRepository);
        Category category = CategoryFixtures.defaultCategory()
                .withThumbnailUrl(FAKE_CLOUD_FRONT_END_POINT + "/" + "images/category.jpg")
                .buildAndSaveWithMember(member, categoryRepository);
        StaccatoFixtures.defaultStaccato()
                .withCategory(category).withStaccatoImages(
                        List.of(FAKE_CLOUD_FRONT_END_POINT + "/" + "images/staccato1-1.jpg",
                                FAKE_CLOUD_FRONT_END_POINT + "/" + "images/staccato1-2.jpg"))
                .buildAndSave(staccatoRepository);
        cloudStorageClient.putS3Object("images/category.jpg", null, null);
        cloudStorageClient.putS3Object("images/category2.jpg", null, null);
        cloudStorageClient.putS3Object("images/staccato1-1.jpg", null, null);
        cloudStorageClient.putS3Object("images/staccato1-2.jpg", null, null);
        cloudStorageClient.putS3Object("images/staccato1-4.jpg", null, null);

        // when
        int deleted = imageService.deleteUnusedImages();

        // then
        assertThat(deleted).isEqualTo(2);
    }

}
