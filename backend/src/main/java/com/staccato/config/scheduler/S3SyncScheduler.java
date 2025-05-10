package com.staccato.config.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.staccato.config.log.annotation.Trace;
import com.staccato.image.service.ImageService;
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
        int deletedCount = imageService.deleteUnusedImages();
        log.info("[S3 이미지 정리] 삭제된 객체 수: {}건", deletedCount);
    }
}
