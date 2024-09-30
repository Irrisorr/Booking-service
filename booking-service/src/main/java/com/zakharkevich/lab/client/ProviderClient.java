package com.zakharkevich.lab.client;

import com.zakharkevich.lab.model.dto.ProviderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "provider-service", url = "${provider-service.url}")
public interface ProviderClient {

    @GetMapping("/{id}")
    ProviderDto getProviderById(@PathVariable Long id);
}