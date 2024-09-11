package com.zakharkevich.lab.model.dto;

import com.zakharkevich.lab.model.entity.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private Long userId;
    private Long providerId;
    private Long serviceId;
    private LocalDateTime visitTime;
    private BookingStatus status;
}