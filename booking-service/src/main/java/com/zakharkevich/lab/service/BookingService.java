package com.zakharkevich.lab.service;

import com.zakharkevich.lab.client.ProviderClient;
import com.zakharkevich.lab.model.dto.NotificationDto;
import com.zakharkevich.lab.model.dto.ProviderDto;
import com.zakharkevich.lab.model.dto.ServiceDto;
import com.zakharkevich.lab.model.entity.Booking;
import com.zakharkevich.lab.model.enums.BookingStatus;
import com.zakharkevich.lab.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ProviderClient providerClient;
    private final JmsTemplate jmsTemplate;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking createBooking(Booking booking) {

        LocalDate bookingDate = booking.getVisitTime().toLocalDate();
        List<String> availableSlots = getAvailableSlots(booking.getProviderId(), booking.getServiceId(), bookingDate);

        if (!availableSlots.contains(booking.getVisitTime().toLocalTime().toString())) {
            throw new RuntimeException("This slot is not available. Available slots: " + availableSlots);
        }

        booking.setStatus(BookingStatus.PENDING);
        Booking savedBooking = bookingRepository.save(booking);

        NotificationDto notification = new NotificationDto(savedBooking.getId(), savedBooking.getStatus());
        jmsTemplate.convertAndSend("bookingNotifications", notification);

        return savedBooking;
    }

    public Booking updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);

        NotificationDto notification = new NotificationDto(updatedBooking.getId(), updatedBooking.getStatus());
        jmsTemplate.convertAndSend("bookingNotifications", notification);

        return updatedBooking;
    }

    public Booking updateBooking(Long id, Booking bookingDetails) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setUserId(bookingDetails.getUserId());
        booking.setProviderId(bookingDetails.getProviderId());
        booking.setServiceId(bookingDetails.getServiceId());
        booking.setVisitTime(bookingDetails.getVisitTime());
        booking.setStatus(bookingDetails.getStatus());
        booking.setStatus(bookingDetails.getStatus());

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public List<String> getAvailableSlots(Long providerId, Long serviceId, LocalDate date) {
        ProviderDto provider = providerClient.getProviderById(providerId);
        ServiceDto service = provider.getServices().stream()
                .filter(s -> s.getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Service not found"));

        LocalTime startTime = provider.getContactInfo().getWorkingHoursStart();
        LocalTime endTime = provider.getContactInfo().getWorkingHoursEnd();

        List<String> availableSlots = new ArrayList<>();
        LocalDateTime currentSlot = LocalDateTime.of(date, startTime);

        while (currentSlot.plusMinutes(service.getDuration()).toLocalTime().isBefore(endTime) ||
                currentSlot.plusMinutes(service.getDuration()).toLocalTime().equals(endTime)) {
            if (isSlotAvailable(providerId, serviceId, currentSlot)) {
                availableSlots.add(currentSlot.toLocalTime().toString());
            }
            currentSlot = currentSlot.plusMinutes(service.getDuration());
        }

        return availableSlots;
    }

    private boolean isSlotAvailable(Long providerId, Long serviceId, LocalDateTime dateTime) {
        List<Booking> conflictingBookings = bookingRepository.findByProviderIdAndServiceIdAndVisitTime(
                providerId, serviceId, dateTime);
        return conflictingBookings.isEmpty();
    }
}