package com.zakharkevich.lab.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer duration;
    private Long providerId;
}
