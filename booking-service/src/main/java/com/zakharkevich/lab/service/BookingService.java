package com.zakharkevich.lab.service;

import com.zakharkevich.lab.client.ProviderClient;
import com.zakharkevich.lab.model.dto.ProviderDto;
import com.zakharkevich.lab.model.dto.ServiceDto;
import com.zakharkevich.lab.model.entity.Booking;
import com.zakharkevich.lab.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ProviderClient providerClient;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking createBooking(Booking booking) {
        if (!isSlotAvailable(booking.getProviderId(), booking.getServiceId(), booking.getVisitTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot is not available");
        }
        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Long id, Booking bookingDetails) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!isSlotAvailable(bookingDetails.getProviderId(), bookingDetails.getServiceId(), bookingDetails.getVisitTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot is not available");
        }

        booking.setUserId(bookingDetails.getUserId());
        booking.setProviderId(bookingDetails.getProviderId());
        booking.setServiceId(bookingDetails.getServiceId());
        booking.setVisitTime(bookingDetails.getVisitTime());

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public List<String> getAvailableSlots(Long providerId, Long serviceId, LocalDate date) {
        ProviderDto provider = providerClient.getProviderById(providerId);
        ServiceDto service = providerClient.getServiceById(providerId, serviceId);

        LocalTime workingHoursStart = provider.getContactInfo().getWorkingHoursStart();
        LocalTime workingHoursEnd = provider.getContactInfo().getWorkingHoursEnd();
        Integer duration = service.getDuration();

        List<LocalDateTime> availableSlots = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.of(date, workingHoursStart);
        LocalDateTime endTime = LocalDateTime.of(date, workingHoursEnd);

        while (startTime.isBefore(endTime)) {
            availableSlots.add(startTime);
            startTime = startTime.plusMinutes(duration);
        }

        List<Booking> bookings = bookingRepository.findByProviderIdAndServiceIdAndVisitTimeBetween(
                providerId, serviceId, LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

        for (Booking booking : bookings) {
            availableSlots.remove(booking.getVisitTime());
        }

        return availableSlots.stream()
                .map(LocalDateTime::toString)
                .collect(Collectors.toList());
    }

    public boolean isSlotAvailable(Long providerId, Long serviceId, LocalDateTime visitTime) {
        ProviderDto provider = providerClient.getProviderById(providerId);
        ServiceDto service = providerClient.getServiceById(providerId, serviceId);

        LocalTime workingHoursStart = provider.getContactInfo().getWorkingHoursStart();
        LocalTime workingHoursEnd = provider.getContactInfo().getWorkingHoursEnd();
        Integer duration = service.getDuration();

        if (visitTime.toLocalTime().isBefore(workingHoursStart) || visitTime.toLocalTime().plusMinutes(duration).isAfter(workingHoursEnd)) {
            return false;
        }

        if (visitTime.toLocalTime().getMinute() % duration != 0) {
            return false;
        }

        List<Booking> bookings = bookingRepository.findByProviderIdAndServiceIdAndVisitTime(providerId, serviceId, visitTime);
        return bookings.isEmpty();
    }
}