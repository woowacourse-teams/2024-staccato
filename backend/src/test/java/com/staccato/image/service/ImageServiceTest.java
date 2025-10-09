package com.staccato.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import com.staccato.ServiceSliceTest;
import com.staccato.category.domain.Category;
import com.staccato.category.repository.CategoryRepository;
import com.staccato.fixture.category.CategoryFixtures;
import com.staccato.fixture.member.MemberFixtures;
import com.staccato.fixture.staccato.StaccatoFixtures;
import com.staccato.image.service.dto.DeletionResult;
import com.staccato.image.service.dto.ImageUrlResponse;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.staccato.repository.StaccatoRepository;

@TestPropertySource(properties = {
        "image.folder.name=test/"
})
public class ImageServiceTest extends ServiceSliceTest {

    @Autowired
    private ImageService imageService;
    @Autowired
    private CloudStorageService cloudStorageService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StaccatoRepository staccatoRepository;

    @Value("${cloud.aws.cloudfront.endpoint}")
    private String cloudFrontEndpoint;

    @Test
    @DisplayName("uploadImage: 파일 업로드 시 URL이 반환되고 key를 다시 추출할 수 있다")
    void uploadImage() {
        // given
        byte[] bytes = "hello".getBytes(StandardCharsets.UTF_8);
        MultipartFile file = new MockMultipartFile("image", "hello.png", "image/png", bytes);

        // when
        ImageUrlResponse response = imageService.uploadImage(file);

        // then
        String url = response.imageUrl();
        String key = cloudStorageService.extractKeyFromUrl(url);

        assertAll(
                () -> assertThat(url).startsWith("http://dummy-cloudfront"),
                () -> assertThat(key).startsWith("test/"),
                () -> assertThat(key).endsWith(".png")
        );
    }

    @DisplayName("사용되지 않은 S3 객체를 삭제한다")
    @Test
    void deleteUnusedImages() {
        // given
        Member member = MemberFixtures.ofDefault().buildAndSave(memberRepository);
        Category category = CategoryFixtures.ofDefault()
                .withThumbnailUrl(cloudFrontEndpoint + "/images/category.jpg")
                .withHost(member)
                .buildAndSave(categoryRepository);

        StaccatoFixtures.ofDefault(category)
                .withStaccatoImages(List.of(cloudFrontEndpoint + "/images/staccato1-1.jpg",
                        cloudFrontEndpoint + "/images/staccato1-2.jpg"))
                .buildAndSave(staccatoRepository);

        cloudStorageService.putS3Object("images/category.jpg", "image/jpeg", new byte[]{});
        cloudStorageService.putS3Object("images/category2.jpg", "image/jpeg", new byte[]{});
        cloudStorageService.putS3Object("images/staccato1-1.jpg", "image/jpeg", new byte[]{});
        cloudStorageService.putS3Object("images/staccato1-2.jpg", "image/jpeg", new byte[]{});
        cloudStorageService.putS3Object("images/staccato1-4.jpg", "image/jpeg", new byte[]{});

        // when
        DeletionResult deleted = imageService.deleteUnusedImages();

        // then
        assertThat(deleted.successCount()).isEqualTo(2);
    }
}
