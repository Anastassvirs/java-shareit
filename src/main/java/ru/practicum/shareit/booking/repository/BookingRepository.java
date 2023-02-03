package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByEndDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(Long userId, LocalDateTime now,
                                                                           LocalDateTime now2);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByEndDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByEndDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByEndDesc(Long userId, StatusOfBooking status);

    List<Booking> findAllByItemOwnerIdOrderByEndDesc(Long userId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByEndDesc(Long userId, LocalDateTime now,
                                                                              LocalDateTime now2);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByEndDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByEndDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByEndDesc(Long userId, StatusOfBooking status);

    List<Booking> findAllByItemOwnerIdAndItemIdAndStartAfterOrderByStartDesc(Long userId, Long itemId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndItemIdAndEndBeforeOrderByEndDesc(Long userId, Long itemId, LocalDateTime now);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartDesc(Long itemId, LocalDateTime now);

    List<Booking> findAllByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);


}
