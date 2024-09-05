package com.zakharkevich.lab.converter;

import com.zakharkevich.lab.model.dto.BookingDto;
import com.zakharkevich.lab.model.entity.Booking;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BookingDtoToBookingEntityConverter implements Converter<BookingDto, Booking> {

    @Override
    public Booking convert(BookingDto source) {
        Booking booking = new Booking();
        booking.setId(source.getId());
        booking.setUserId(source.getUserId());
        booking.setVisitTime(source.getVisitTime());
        return booking;
    }
}