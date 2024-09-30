package com.zakharkevich.lab.model.dto;

import com.zakharkevich.lab.model.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto implements Serializable {
    private Long bookingId;
    private BookingStatus status;
}