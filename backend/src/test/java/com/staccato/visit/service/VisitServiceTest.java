package com.staccato.visit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import com.staccato.ServiceSliceTest;
import com.staccato.exception.StaccatoException;
import com.staccato.member.domain.Member;
import com.staccato.member.repository.MemberRepository;
import com.staccato.travel.domain.Travel;
import com.staccato.travel.repository.TravelRepository;
import com.staccato.visit.domain.Visit;
import com.staccato.visit.domain.VisitImages;
import com.staccato.visit.domain.VisitLog;
import com.staccato.visit.fixture.VisitFixture;
import com.staccato.visit.fixture.VisitLogFixture;
import com.staccato.visit.repository.VisitImageRepository;
import com.staccato.visit.repository.VisitLogRepository;
import com.staccato.visit.repository.VisitRepository;
import com.staccato.visit.service.dto.request.VisitRequest;
import com.staccato.visit.service.dto.request.VisitUpdateRequest;
import com.staccato.visit.service.dto.response.VisitDetailResponse;

class VisitServiceTest extends ServiceSliceTest {
    @Autowired
    private VisitService visitService;
    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private VisitLogRepository visitLogRepository;
    @Autowired
    private VisitImageRepository visitImageRepository;
    @Autowired
    private TravelRepository travelRepository;
    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("사진 없이도 방문 기록을 생성할 수 있다.")
    @Test
    void createVisit() {
        // given
        travelRepository.save(
                Travel.builder().title("Sample Travel").startAt(LocalDate.now()).endAt(LocalDate.now().plusDays(1))
                        .build()
        );

        // when
        long visitId = visitService.createVisit(getVisitRequestWithoutImage()).visitId();

        // then
        assertThat(visitRepository.findById(visitId)).isNotEmpty();
    }

    private VisitRequest getVisitRequestWithoutImage() {
        return new VisitRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, List.of(), LocalDate.now(), 1L);
    }

    @DisplayName("방문 기록을 생성하면 Visit과 VisitImage들이 함께 저장되고 id를 반환한다.")
    @Test
    void createVisitWithVisitImages() {
        // given
        travelRepository.save(
                Travel.builder().title("Sample Travel").startAt(LocalDate.now()).endAt(LocalDate.now().plusDays(1))
                        .build()
        );

        // when
        long visitId = visitService.createVisit(getVisitRequest()).visitId();

        // then
        assertAll(
                () -> assertThat(visitRepository.findById(visitId)).isNotEmpty(),
                () -> assertThat(visitImageRepository.findFirstByVisitId(visitId)).isNotEmpty()
        );
    }

    @DisplayName("존재하지 않는 여행에 방문 기록 생성을 시도하면 예외가 발생한다.")
    @Test
    void failCreateVisit() {
        assertThatThrownBy(() -> visitService.createVisit(getVisitRequest()))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 여행을 찾을 수 없어요.");
    }

    private VisitRequest getVisitRequest() {
        return new VisitRequest("placeName", "address", BigDecimal.ONE, BigDecimal.ONE, List.of("https://example1.com.jpg"), LocalDate.now(), 1L);
    }

    @DisplayName("특정 방문 기록 조회에 성공한다.")
    @Test
    void readVisitById() {
        // given
        Travel travel = travelRepository.save(
                Travel.builder().title("Sample Travel").startAt(LocalDate.now()).endAt(LocalDate.now().plusDays(1))
                        .build()
        );
        Visit visit = visitRepository.save(VisitFixture.create(travel, LocalDate.now()));

        // when
        VisitDetailResponse actual = visitService.readVisitById(visit.getId());

        // then
        assertThat(actual).isEqualTo(new VisitDetailResponse(visit));
    }

    @DisplayName("존재하지 않는 방문 기록을 조회하면 예외가 발생한다.")
    @Test
    void failReadVisitById() {
        // given & when & then
        assertThatThrownBy(() -> visitService.readVisitById(1L))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 방문 기록을 찾을 수 없어요.");
    }

    @DisplayName("특정 방문 기록 수정에 성공한다.")
    @Test
    void updateVisitById() {
        // given
        Travel travel = travelRepository.save(
                Travel.builder().title("Sample Travel").startAt(LocalDate.now().minusDays(1))
                        .endAt(LocalDate.now().plusDays(1)).build()
        );
        Visit visit = VisitFixture.create(travel, LocalDate.now());
        visit.addVisitImages(new VisitImages(List.of("https://oldExample.com.jpg", "https://existExample.com.jpg")));
        visitRepository.save(visit);

        // when
        VisitUpdateRequest visitUpdateRequest = new VisitUpdateRequest("newPlaceName", List.of("https://existExample.com.jpg"));
        MockMultipartFile mockMultipartFile = new MockMultipartFile("visitImagesFile", "newExample.jpg".getBytes());
        visitService.updateVisitById(visit.getId(), visitUpdateRequest, List.of(mockMultipartFile));

        // then
        Visit foundedVisit = visitRepository.findById(visit.getId()).get();
        assertAll(
                () -> assertThat(foundedVisit.getPlaceName()).isEqualTo("newPlaceName"),
                () -> assertThat(visitImageRepository.findById(1L)).isEmpty(),
                () -> assertThat(visitImageRepository.findById(2L).get()
                        .getImageUrl()).isEqualTo("https://existExample.com.jpg"),
                () -> assertThat(visitImageRepository.findById(3L).get().getImageUrl()).isEqualTo("visitImagesFile"),
                () -> assertThat(visitImageRepository.findById(2L).get().getVisit()
                        .getId()).isEqualTo(foundedVisit.getId()),
                () -> assertThat(visitImageRepository.findById(3L).get().getVisit()
                        .getId()).isEqualTo(foundedVisit.getId()),
                () -> assertThat(visitImageRepository.findAll().size()).isEqualTo(2)
        );
    }

    @DisplayName("존재하지 않는 방문 기록을 수정하면 예외가 발생한다.")
    @Test
    void failUpdateVisitById() {
        // given
        VisitUpdateRequest visitUpdateRequest = new VisitUpdateRequest("placeName", List.of("https://example1.com.jpg"));

        // when & then
        assertThatThrownBy(() -> visitService.updateVisitById(1L, visitUpdateRequest, List.of(new MockMultipartFile("visitImagesFile", "namsan_tower.jpg".getBytes()))))
                .isInstanceOf(StaccatoException.class)
                .hasMessageContaining("요청하신 방문 기록을 찾을 수 없어요.");
    }

    @DisplayName("Visit을 삭제하면 이에 포함된 VisitImage와 VisitLog도 모두 삭제된다.")
    @Test
    void deleteVisitById() {
        // given
        Member member = memberRepository.save(Member.builder().nickname("SampleMember").build());
        Travel travel = travelRepository.save(
                Travel.builder().title("Sample Travel").startAt(LocalDate.now()).endAt(LocalDate.now().plusDays(1))
                        .build()
        );
        Visit visit = visitRepository.save(VisitFixture.create(travel, LocalDate.now()));
        VisitLog visitLog = visitLogRepository.save(VisitLogFixture.create(visit, member));
        visit.addVisitImages(new VisitImages(List.of("https://oldExample.com.jpg", "https://existExample.com.jpg")));

        // when
        visitService.deleteVisitById(visit.getId());

        // then
        assertAll(
                () -> assertThat(visitRepository.findById(visit.getId())).isEmpty(),
                () -> assertThat(visitLogRepository.findById(visitLog.getId())).isEmpty(),
                () -> assertThat(visitImageRepository.findById(0L)).isEmpty(),
                () -> assertThat(visitImageRepository.findById(1L)).isEmpty()
        );
    }
}
