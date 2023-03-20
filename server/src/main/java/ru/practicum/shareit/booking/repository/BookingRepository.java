package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerIdOrderByEndDesc(Long userId, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(Long userId, LocalDateTime now,
                                                                           LocalDateTime now2, Pageable pageable);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByEndDesc(Long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByEndDesc(Long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByBookerIdAndStatusOrderByEndDesc(Long userId, StatusOfBooking status, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdOrderByEndDesc(Long userId, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByEndDesc(Long userId, LocalDateTime now,
                                                                              LocalDateTime now2, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndEndBeforeOrderByEndDesc(Long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStartAfterOrderByEndDesc(Long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdAndStatusOrderByEndDesc(Long userId, StatusOfBooking status, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndItemIdAndStartAfterOrderByStartDesc(Long userId, Long itemId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndItemIdAndEndBeforeOrderByEndDesc(Long userId, Long itemId, LocalDateTime now);

    List<Booking> findAllByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    List<Booking> findAllByItemIdAndStartAfter(Long id, LocalDateTime now);

    List<Booking> findAllByItemIdAndEndBefore(Long id, LocalDateTime now);
}
