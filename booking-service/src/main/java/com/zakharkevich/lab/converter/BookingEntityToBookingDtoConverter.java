package com.zakharkevich.lab.converter;

import com.zakharkevich.lab.model.dto.BookingDto;
import com.zakharkevich.lab.model.entity.Booking;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BookingEntityToBookingDtoConverter implements Converter<Booking, BookingDto> {

    @Override
    public BookingDto convert(Booking source) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(source.getId());
        bookingDto.setUserId(source.getUserId());
        bookingDto.setVisitTime(source.getVisitTime());
        return bookingDto;
    }
}