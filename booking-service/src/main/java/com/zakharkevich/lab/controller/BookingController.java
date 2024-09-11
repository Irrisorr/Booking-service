package com.zakharkevich.lab.controller;

import com.zakharkevich.lab.model.dto.BookingDto;
import com.zakharkevich.lab.model.entity.Booking;
import com.zakharkevich.lab.model.entity.BookingStatus;
import com.zakharkevich.lab.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final ConversionService conversionService;


    @GetMapping
    @PreAuthorize("hasAuthority('provider.read')")
    public List<BookingDto> getAllBookings() {
        return bookingService.getAllBookings().stream()
                .map(booking -> conversionService.convert(booking, BookingDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('provider.read')")
    public BookingDto getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return conversionService.convert(booking, BookingDto.class);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('provider.write')")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody BookingDto bookingDto) {
        Booking booking = conversionService.convert(bookingDto, Booking.class);
        Booking createdBooking = bookingService.createBooking(booking);
        return conversionService.convert(createdBooking, BookingDto.class);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('provider.write')")
    public BookingDto updateBooking(@PathVariable Long id, @RequestBody BookingDto bookingDto) {
        Booking booking = conversionService.convert(bookingDto, Booking.class);
        Booking updatedBooking = bookingService.updateBooking(id, booking);
        return conversionService.convert(updatedBooking, BookingDto.class);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('provider.write')")
    public BookingDto approveBooking(@PathVariable Long id) {
        Booking updatedBooking = bookingService.updateBookingStatus(id, BookingStatus.APPROVED);
        return conversionService.convert(updatedBooking, BookingDto.class);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('provider.write')")
    public BookingDto rejectBooking(@PathVariable Long id) {
        Booking updatedBooking = bookingService.updateBookingStatus(id, BookingStatus.REJECTED);
        return conversionService.convert(updatedBooking, BookingDto.class);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('provider.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }

    @GetMapping("/providers/{providerId}/services/{serviceId}/availability")
    @PreAuthorize("hasAuthority('provider.read')")
    public List<String> getAvailableSlots(@PathVariable Long providerId, @PathVariable Long serviceId, @RequestParam LocalDate date) {
        return bookingService.getAvailableSlots(providerId, serviceId, date);
    }

}