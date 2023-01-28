package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;

import java.util.List;

@Component
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Мне кажется, что этот набор можно оптимизировать, но я не нашла способа, как.
    // Если разделять строку запроса на две части(чтобы создать вариативность), то вторая часть не видит, что такое b
    @Query(value = "select b from Booking b where b.booker.id = ?1")
    List<Booking> findAllByUser(Long userId);

    @Query(value = "select b from Booking b where " +
            "b.booker.id = ?1 and current_timestamp > b.start and current_timestamp < b.end")
    List<Booking> findCurrentByUser(Long userId);

    @Query(value = "select b from Booking b where " +
            "b.booker.id = ?1 and current_timestamp > b.end")
    List<Booking> findPastByUser(Long userId);

    @Query(value = "select b from Booking b where " +
            "b.booker.id = ?1 and current_timestamp < b.start")
    List<Booking> findFutureByUser(Long userId);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.status = ?2")
    List<Booking> findByStatusAndUser(Long userId, StatusOfBooking status);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1")
    List<Booking> findAllByOwner(Long userId);

    @Query(value = "select b from Booking b where " +
            "b.item.owner.id = ?1 and current_timestamp > b.start and current_timestamp < b.end")
    List<Booking> findCurrentByOwner(Long userId);

    @Query(value = "select b from Booking b where " +
            "b.item.owner.id = ?1 and current_timestamp > b.end")
    List<Booking> findPastByOwner(Long userId);

    @Query(value = "select b from Booking b where " +
            "b.item.owner.id = ?1 and current_timestamp < b.start")
    List<Booking> findFutureByOwner(Long userId);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.status = ?2")
    List<Booking> findByStatusAndOwner(Long userId, StatusOfBooking status);

    // Можно ли сразу передать только первый элемент? TOP 1 тут не работает, не могу найти решение
    @Query(value = "select b from Booking b where b.item.id = ?1 and b.start > current_timestamp order by b.start")
    List<Booking> findNextBookingsByItem(Long itemId);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.start < current_timestamp order by b.end desc")
    List<Booking> findPastBookingsByItem(Long itemId);
}
