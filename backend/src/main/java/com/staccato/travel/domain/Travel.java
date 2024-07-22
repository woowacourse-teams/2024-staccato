package com.staccato.travel.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.hibernate.annotations.SQLDelete;

import com.staccato.config.domain.BaseEntity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE travel SET is_deleted = true WHERE id = ?")
public class Travel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;
    @Column(nullable = false, length = 50)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private LocalDate startAt;
    @Column(nullable = false)
    private LocalDate endAt;
}
