package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.exceptions.WrongParametersException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingsComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public List<Item> findAll() {
        return repository.findAll();
    }

    private ItemDtoBookingsComments upgradeItem(Item item) {
        ItemDtoBookingsComments fullItem;
        fullItem = ItemMapper.toItemDtoBookingsComments(item);
        List<Booking> bookings;
        bookings = bookingRepository.findAllByItemIdAndStartAfterOrderByStartDesc(item.getId(), LocalDateTime.now());
        if (!bookings.isEmpty()) {
            Booking booking = bookings.stream().findFirst().orElse(null);
            if (!Objects.isNull(booking)) {
                BookingDto nextBooking = bookingMapper.toBookingDto(booking);
                fullItem.setNextBooking(nextBooking);
            }
        }
        bookings = bookingRepository.findAllByItemIdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());

        if (!bookings.isEmpty()) {
            Booking booking = bookings.stream().findFirst().orElse(null);
            if (!Objects.isNull(booking)) {
                BookingDto lastBooking = bookingMapper.toBookingDto(booking);
                fullItem.setLastBooking(lastBooking);
            }
        }
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(commentMapper.toCommentDto(comment));
        }
        fullItem.setComments(commentsDto);
        return fullItem;
    }

    @Override
    public List<ItemDtoBookingsComments> findAllByUser(Long userId) {
        if (!userService.userExistById(userId)) {
            throw new NotFoundAnythingException("Пользователя, по которому производится поиск вещи, не существует");
        }
        List<Item> items = repository.findAllByOwnerIdOrderById(userId);
        List<ItemDtoBookingsComments> fullItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getAvailable()) {
                fullItems.add(upgradeItem(item));
            }
        }
        return fullItems;
    }

    @Override
    public List<ItemDtoBookingsComments> findAllByText(String text, Long userId) {
        if (!userService.userExistById(userId)) {
            throw new NotFoundAnythingException("Пользователя, от лица которого производится поиск вещи, не существует");
        }
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        List<Item> items = repository.findAllByText(text);
        List<ItemDtoBookingsComments> fullItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getAvailable()) {
                fullItems.add(upgradeItem(item));
            }
        }
        return fullItems;
    }

    @Override
    public ItemDtoBookingsComments findDtoById(Long id, Long userId) {
        if (!userService.userExistById(userId)) {
            throw new NotFoundAnythingException("Пользователя, от лица которого производится поиск вещи, не существует");
        }
        Item item = findById(id);
        ItemDtoBookingsComments fullItem;
        fullItem = ItemMapper.toItemDtoBookingsComments(item);
        List<Booking> bookings;
        bookings = bookingRepository.findAllByItemOwnerIdAndItemIdAndStartAfterOrderByStartDesc(userId, item.getId(),
                LocalDateTime.now());
        if (!bookings.isEmpty()) {
            Booking booking = bookings.stream().findFirst().orElse(null);
            if (!Objects.isNull(booking)) {
                BookingDto nextBooking = bookingMapper.toBookingDto(booking);
                fullItem.setNextBooking(nextBooking);
            }
        }
        bookings = bookingRepository.findAllByItemOwnerIdAndItemIdAndEndBeforeOrderByEndDesc(userId, item.getId(),
                LocalDateTime.now());

        if (!bookings.isEmpty()) {
            Booking booking = bookings.stream().findFirst().orElse(null);
            if (!Objects.isNull(booking)) {
                BookingDto lastBooking = bookingMapper.toBookingDto(booking);
                fullItem.setLastBooking(lastBooking);
            }
        }
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(commentMapper.toCommentDto(comment));
        }
        fullItem.setComments(commentsDto);
        return fullItem;
    }

    @Override
    public Item findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundAnythingException("Вещи с данным id не существует"));
    }

    @Override
    @Transactional
    public Item createItem(ItemDto itemDto, Long ownerId) {
        if (!userService.userExistById(ownerId)) {
            throw new NotFoundAnythingException("Пользователя, от лица которого производится создание вещи, не существует");
        }
        log.debug("Пользователем с id: {} была добавлена новая вещь: {}", ownerId, itemDto);
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(userService.findById(ownerId));
        return repository.save(newItem);
    }

    @Override
    @Transactional
    public Item updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        if (!userService.userExistById(ownerId)) {
            throw new NotFoundAnythingException("Пользователя, от лица которого производится изменение вещи, не существует");
        }
        if (!itemExistById(itemId)) {
            throw new NotFoundAnythingException("Вещи, которую вы пытаетесь изменить, не существует");
        }
        Item item = findById(itemId);
        if (ownerId.equals(item.getOwner().getId())) {
            Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
            Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
            Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);
            log.debug("Обновлена вещь: {}", item);
            return repository.save(item);
        } else {
            log.debug("Произошла ошибка: ID пользователя не соответсвует владельцу вещи");
            throw new NotFoundAnythingException("ID пользователя не соответсвует владельцу вещи");
        }

    }

    @Override
    @Transactional
    public void deleteItem(Long itemId, Long userId) {
        if (!userService.userExistById(userId)) {
            throw new NotFoundAnythingException("Пользователя, от лица которого удаляется комментарий, не существует");
        }
        if (!itemExistById(itemId)) {
            throw new NotFoundAnythingException("Вещи, которую вы пытаетесь удалить, не существует");
        }
        if (!userId.equals(findById(itemId).getOwner().getId())) {
            log.debug("Произошла ошибка: ID пользователя не соответсвует владельцу вещи");
            throw new NotFoundAnythingException("ID пользователя не соответсвует владельцу вещи");

        }
        log.debug("Удалена вещь с id : {}", itemId);
        repository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        if (!userService.userExistById(userId)) {
            throw new NotFoundAnythingException("Пользователя, от лица которого создается комментарий, не существует");
        }
        if (!itemExistById(itemId)) {
            throw new NotFoundAnythingException("Вещи, к которой создается комментарий, не существует");
        }
        if (commentDto.getText().equals("")) {
            throw new WrongParametersException("Поле текста комментария не  может быть пустым");
        }
        User author = userService.findById(userId);
        List<Booking> bookings = bookingRepository.findAllByItemIdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now());
        boolean isBookedByThatUser = false;
        for (Booking booking : bookings) {
            if (booking.getBooker().getId().equals(userId)) {
                isBookedByThatUser = true;
            }
        }
        if (Boolean.FALSE.equals(isBookedByThatUser)) {
            throw new WrongParametersException("Написать отзыв может только человек, бронировавший вещь!");
        }
        Comment comment = commentMapper.newtoComment(commentDto, findById(itemId), author);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public boolean itemExistById(Long id) {
        for (Item oldItem : repository.findAll()) {
            if (Objects.equals(oldItem.getId(), id)) {
                return true;
            }
        }
        return false;
    }


}
