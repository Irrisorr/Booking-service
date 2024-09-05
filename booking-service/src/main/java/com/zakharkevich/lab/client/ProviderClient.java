package com.zakharkevich.lab.client;

import com.zakharkevich.lab.model.dto.ProviderDto;
import com.zakharkevich.lab.model.dto.ServiceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "provider-service", url = "http://localhost:8081/api/providers")
public interface ProviderClient {

    @GetMapping("/{id}")
    ProviderDto getProviderById(@PathVariable Long id);

    @GetMapping("/{providerId}/services/{serviceId}")
    ServiceDto getServiceById(@PathVariable Long providerId, @PathVariable Long serviceId);
}