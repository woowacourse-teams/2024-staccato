package com.staccato.moment.domain.support;

import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;
import com.staccato.moment.domain.Moment;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MomentEntityListener {
    @PrePersist
    @PreUpdate
    @PreRemove
    public void touchForWrite(Object target) {
        if (Objects.isNull(target)) {
            log.debug("Entity must not be null");
        }
        if (!(target instanceof Moment)) {
            log.debug("Entity must be instance of Moment");
        }

        if (target instanceof Moment moment) {
            moment.getMemory().setUpdatedAt(LocalDateTime.now());
        }
    }
}
