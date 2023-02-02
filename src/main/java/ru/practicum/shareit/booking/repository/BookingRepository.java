package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select b from Booking b where b.booker.id = ?1 order by b.end desc")
    List<Booking> findAllByUser(Long userId);

    @Query(value = "select b from Booking b where " +
            "b.booker.id = ?1 and current_timestamp > b.start and current_timestamp < b.end order by b.end desc")
    List<Booking> findCurrentByUser(Long userId);

    @Query(value = "select b from Booking b where " +
            "b.booker.id = ?1 and current_timestamp > b.end order by b.end desc")
    List<Booking> findPastByUser(Long userId);

    @Query(value = "select b from Booking b where " +
            "b.booker.id = ?1 and current_timestamp < b.start order by b.end desc")
    List<Booking> findFutureByUser(Long userId);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.status = ?2 order by b.end desc")
    List<Booking> findByStatusAndUser(Long userId, StatusOfBooking status);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 order by b.end desc")
    List<Booking> findAllByOwner(Long userId);

    @Query(value = "select b from Booking b where " +
            "b.item.owner.id = ?1 and current_timestamp > b.start and current_timestamp < b.end order by b.end desc")
    List<Booking> findCurrentByOwner(Long userId);

    @Query(value = "select b from Booking b where " +
            "b.item.owner.id = ?1 and current_timestamp > b.end order by b.end desc")
    List<Booking> findPastByOwner(Long userId);

    @Query(value = "select b from Booking b where " +
            "b.item.owner.id = ?1 and current_timestamp < b.start order by b.end desc")
    List<Booking> findFutureByOwner(Long userId);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.end desc")
    List<Booking> findByStatusAndOwner(Long userId, StatusOfBooking status);

    // Можно ли сразу передать только первый элемент? TOP 1 тут не работает, не могу найти решение
    @Query(value = "select b from Booking b where b.item.id = ?1 and b.start > current_timestamp order by b.start desc")
    List<Booking> findNextBookingsByItem(Long itemId);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.end < current_timestamp order by b.end desc")
    List<Booking> findPastBookingsByItem(Long itemId);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.start > current_timestamp order by b.start desc")
    List<Booking> findNextBookingsByItemAndUser(Long itemId, Long userId);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.end < current_timestamp order by b.end desc")
    List<Booking> findPastBookingsByItemAndUser(Long itemId, Long userId);
}
