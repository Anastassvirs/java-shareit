package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {
    private String name;
    private String description;
    private boolean available;
    private Long along;

    public ItemDto(String name, String description, boolean available, Long along) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.along = along;
    }
}
