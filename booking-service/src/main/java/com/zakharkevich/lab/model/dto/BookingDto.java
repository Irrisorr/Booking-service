package com.zakharkevich.lab.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private Long userId;
    private LocalDateTime visitTime;
}