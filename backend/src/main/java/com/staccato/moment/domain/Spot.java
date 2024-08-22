package com.staccato.moment.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Spot {
    @Column(nullable = false)
    private String address;
    @Column(nullable = false, columnDefinition = "DECIMAL(16, 14)")
    private BigDecimal latitude;
    @Column(nullable = false, columnDefinition = "DECIMAL(17, 14)")
    private BigDecimal longitude;
}
