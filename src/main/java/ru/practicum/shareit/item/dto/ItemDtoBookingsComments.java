package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class ItemDtoBookingsComments {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;

    public ItemDtoBookingsComments(Long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }

    public ItemDtoBookingsComments(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDtoBookingsComments)) return false;
        ItemDtoBookingsComments that = (ItemDtoBookingsComments) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getAvailable(), that.getAvailable()) && Objects.equals(getRequestId(), that.getRequestId()) && Objects.equals(getLastBooking(), that.getLastBooking()) && Objects.equals(getNextBooking(), that.getNextBooking()) && Objects.equals(getComments(), that.getComments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getAvailable(), getRequestId(), getLastBooking(), getNextBooking(), getComments());
    }
}
