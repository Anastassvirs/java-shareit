package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
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

import static java.util.Comparator.comparing;

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
    private final ItemMapper itemMapper;

    @Override
    public List<Item> findAll() {
        return repository.findAll();
    }

    private BookingDto findNextBooking(Long itemId) {
        System.out.println(LocalDateTime.now());
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStartAfterAndStatus(itemId,
                LocalDateTime.now(), StatusOfBooking.APPROVED);
        System.out.println("\nnextbookings: " + bookings + "\n");
        if (!bookings.isEmpty()) {
            Booking nextBooking = bookings.stream()
                    .min(comparing(Booking::getStart))
                    .orElse(null);
            System.out.println("\nnextbooking: " + nextBooking + "\n");
            return bookingMapper.toBookingDto(nextBooking);
        }
        System.out.println("\nnextbooking: " + null + "\n");
        return null;
    }

    private BookingDto findLastBooking(Long itemId) {
        System.out.println(LocalDateTime.now());
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStartBeforeAndStatus(itemId,
                LocalDateTime.now(), StatusOfBooking.APPROVED);
        System.out.println("\nlastbookings: " + bookings + "\n");
        if (!bookings.isEmpty()) {
            Booking nextBooking = bookings.stream()
                    .max(comparing(Booking::getEnd))
                    .orElse(null);
            System.out.println("\nlastbooking: " + nextBooking + "\n");
            return bookingMapper.toBookingDto(nextBooking);
        }
        System.out.println("\nlastbooking: " + null + "\n");
        return null;
    }

    private ItemDtoBookingsComments upgradeItem(Item item, Long userId) {
        ItemDtoBookingsComments fullItem = itemMapper.toItemDtoBookingsComments(item);

        if (item.getOwner().getId().equals(userId)) {
            BookingDto nextBookingDto = findNextBooking(item.getId());
            fullItem.setNextBooking(nextBookingDto);
            BookingDto lastBookingDto = findLastBooking(item.getId());
            fullItem.setLastBooking(lastBookingDto);
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
    public List<ItemDtoBookingsComments> findAllByUser(Integer from, Integer size, Long userId) {
        if (!userService.userExistById(userId)) {
            throw new NotFoundAnythingException("Пользователя, по которому производится поиск вещи, не существует");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> items = repository.findAllByOwnerIdOrderById(userId, pageable);
        List<ItemDtoBookingsComments> fullItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getAvailable()) {
                fullItems.add(upgradeItem(item, userId));
            }
        }
        return fullItems;
    }

    @Override
    public List<ItemDtoBookingsComments> findAllByText(Integer from, Integer size, String text, Long userId) {
        if (!userService.userExistById(userId)) {
            throw new NotFoundAnythingException("Пользователя, от лица которого производится поиск вещи, не существует");
        }
        if (StringUtils.isBlank(text) || text.equals("")) {
            return List.of();
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> items = repository.findAllByText(text, pageable);
        List<ItemDtoBookingsComments> fullItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getAvailable()) {
                fullItems.add(upgradeItem(item, userId));
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
        return upgradeItem(item, userId);
    }

    @Override
    public Item findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundAnythingException("Вещи с данным id не существует"));
    }

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        if (!userService.userExistById(ownerId)) {
            throw new NotFoundAnythingException("Пользователя, от лица которого производится создание вещи, не существует");
        }
        log.debug("Пользователем с id: {} была добавлена новая вещь: {}", ownerId, itemDto);
        Item newItem = itemMapper.toItem(itemDto);
        newItem.setOwner(userService.findById(ownerId));
        return itemMapper.toItemDto(repository.save(newItem));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
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
            return itemMapper.toItemDto(repository.save(item));
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
        Comment comment = commentMapper.newtoComment(commentDto, findById(itemId), author, LocalDateTime.now());
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
