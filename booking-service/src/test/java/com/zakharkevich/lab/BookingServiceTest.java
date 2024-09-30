package com.zakharkevich.lab;

import com.zakharkevich.lab.client.ProviderClient;
import com.zakharkevich.lab.model.dto.ContactInfoDto;
import com.zakharkevich.lab.model.dto.NotificationDto;
import com.zakharkevich.lab.model.dto.ProviderDto;
import com.zakharkevich.lab.model.dto.ServiceDto;
import com.zakharkevich.lab.model.entity.Booking;
import com.zakharkevich.lab.model.enums.BookingStatus;
import com.zakharkevich.lab.repository.BookingRepository;
import com.zakharkevich.lab.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ProviderClient providerClient;

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllBookings() {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        Booking booking2 = new Booking();
        booking2.setId(2L);
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnBookingById() {
        Booking booking = new Booking();
        booking.setId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Optional<Booking> result = bookingService.getBookingById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void shouldCreateBookingAndSendNotification() {
        Booking booking = new Booking();
        booking.setProviderId(1L);
        booking.setServiceId(1L);
        booking.setVisitTime(LocalDateTime.of(2023, 1, 1, 10, 0));

        ProviderDto providerDto = new ProviderDto();
        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setWorkingHoursStart(LocalTime.of(9, 0));
        contactInfoDto.setWorkingHoursEnd(LocalTime.of(18, 0));
        providerDto.setContactInfo(contactInfoDto);
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setId(1L);
        serviceDto.setDuration(60);
        providerDto.setServices(List.of(serviceDto));

        when(providerClient.getProviderById(1L)).thenReturn(providerDto);
        when(bookingRepository.findByProviderIdAndServiceIdAndVisitTime(anyLong(), anyLong(), any())).thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.createBooking(booking);

        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, result.getStatus());
        verify(bookingRepository, times(1)).save(booking);
        verify(jmsTemplate, times(1)).convertAndSend(eq("bookingNotifications"), any(NotificationDto.class));
    }

    @Test
    void shouldThrowExceptionWhenSlotIsNotAvailable() {
        Booking booking = new Booking();
        booking.setProviderId(1L);
        booking.setServiceId(1L);
        booking.setVisitTime(LocalDateTime.of(2023, 1, 1, 10, 0));

        ProviderDto providerDto = new ProviderDto();
        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setWorkingHoursStart(LocalTime.of(9, 0));
        contactInfoDto.setWorkingHoursEnd(LocalTime.of(18, 0));
        providerDto.setContactInfo(contactInfoDto);
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setId(1L);
        serviceDto.setDuration(60);
        providerDto.setServices(List.of(serviceDto));

        when(providerClient.getProviderById(1L)).thenReturn(providerDto);
        when(bookingRepository.findByProviderIdAndServiceIdAndVisitTime(anyLong(), anyLong(), any())).thenReturn(List.of(booking));

        assertThrows(RuntimeException.class, () -> bookingService.createBooking(booking));
        verify(bookingRepository, never()).save(booking);
        verify(jmsTemplate, never()).convertAndSend(anyString(), any(NotificationDto.class));
    }

    @Test
    void shouldSendNotificationWhenBookingApproved() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.updateBookingStatus(1L, BookingStatus.APPROVED);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).save(booking);
        verify(jmsTemplate, times(1)).convertAndSend(eq("bookingNotifications"), any(NotificationDto.class));
    }

    @Test
    void shouldReturnAvailableSlots() {
        Long providerId = 1L;
        Long serviceId = 1L;
        LocalDate date = LocalDate.of(2023, 1, 1);

        ProviderDto providerDto = new ProviderDto();
        ContactInfoDto contactInfoDto = new ContactInfoDto();
        contactInfoDto.setWorkingHoursStart(LocalTime.of(9, 0));
        contactInfoDto.setWorkingHoursEnd(LocalTime.of(18, 0));
        providerDto.setContactInfo(contactInfoDto);
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setId(serviceId);
        serviceDto.setDuration(60);
        providerDto.setServices(List.of(serviceDto));

        when(providerClient.getProviderById(providerId)).thenReturn(providerDto);
        when(bookingRepository.findByProviderIdAndServiceIdAndVisitTime(anyLong(), anyLong(), any())).thenReturn(List.of());

        List<String> result = bookingService.getAvailableSlots(providerId, serviceId, date);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(providerClient, times(1)).getProviderById(providerId);
    }
}