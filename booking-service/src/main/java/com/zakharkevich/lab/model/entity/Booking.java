package com.zakharkevich.lab.model.entity;

import com.zakharkevich.lab.model.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long providerId;

    private Long serviceId;

    private LocalDateTime visitTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}

