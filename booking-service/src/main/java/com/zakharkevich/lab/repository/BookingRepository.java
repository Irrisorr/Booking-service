package com.zakharkevich.lab.repository;

import com.zakharkevich.lab.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByProviderIdAndServiceIdAndVisitTimeBetween(Long providerId, Long serviceId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByProviderIdAndServiceIdAndVisitTime(Long providerId, Long serviceId, LocalDateTime visitTime);
}