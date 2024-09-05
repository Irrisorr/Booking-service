package com.zakharkevich.lab.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProviderDto {
    private Long id;
    private String name;
    private String description;
    private List<ServiceDto> services;
    private ContactInfoDto contactInfo;
    private String imageUrl;
}