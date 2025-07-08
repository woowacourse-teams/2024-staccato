package com.staccato.config.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.staccato.config.log.annotation.Trace;
import com.staccato.image.service.ImageService;
import com.staccato.image.service.dto.DeletionResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Trace
@Component
@RequiredArgsConstructor
@Slf4j
public class S3SyncScheduler {

    private final ImageService imageService;

    @Scheduled(cron = "0 0 3 * * *")
    public void deleteOrphanS3Objects() {
        DeletionResult result = imageService.deleteUnusedImages();
        log.info("S3 이미지 삭제 결과: 성공={} 실패={}", result.successCount(), result.failedCount());
    }
}
