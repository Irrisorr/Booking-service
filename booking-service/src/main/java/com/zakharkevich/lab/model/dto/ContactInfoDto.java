package com.zakharkevich.lab.model.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class ContactInfoDto {
    private Long id;
    private String city;
    private String street;
    private String house;
    private String phone;
    private LocalTime workingHoursStart;
    private LocalTime workingHoursEnd;
}
