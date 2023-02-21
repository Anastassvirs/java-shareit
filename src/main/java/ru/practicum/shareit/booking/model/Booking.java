package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
public class Booking {

    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private StatusOfBooking status;

    public Booking(LocalDateTime start, LocalDateTime end, Item item) {
        this.start = start;
        this.end = end;
        this.item = item;
    }

    public Booking(Long id, LocalDateTime start, LocalDateTime end, Item item) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking booking = (Booking) o;
        return Objects.equals(getId(), booking.getId()) && Objects.equals(getStart(), booking.getStart()) && Objects.equals(getEnd(), booking.getEnd()) && Objects.equals(getItem(), booking.getItem()) && Objects.equals(getBooker(), booking.getBooker()) && getStatus() == booking.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStart(), getEnd(), getItem(), getBooker(), getStatus());
    }
}
