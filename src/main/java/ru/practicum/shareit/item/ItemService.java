package ru.practicum.shareit.item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.IllegalRequestException;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
<<<<<<< HEAD
import ru.practicum.shareit.item.dto.CreateItemRequest;
=======
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional
<<<<<<< HEAD
    public ItemDto createItem(CreateItemRequest itemDto, Long userId) {
=======
    public ItemDto createItem(ItemDto itemDto, Long userId) {
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundValidationException("Owner with id: " + userId + " not found"));
        Item newItem = itemMapper.toItem(itemDto);
        newItem.setOwner(owner);
        log.info("Item created" + newItem);
        return itemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Transactional
<<<<<<< HEAD
    public ItemDto updateItem(CreateItemRequest itemDto, Long itemId, Long userId) {
=======
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundValidationException("Item with id: " + itemId + " not found"));
        if (!Objects.equals(oldItem.getOwner().getId(), userId))
            throw new NotFoundValidationException("User not owner");
        Item item  = itemMapper.toItem(itemDto);
        Item itemNew  = itemOwnerNameDescAvailValidator(item, oldItem);
        log.info("Item updated" + itemNew);
        return itemMapper.toItemDto(itemNew);
    }

    @Transactional(readOnly = true)
<<<<<<< HEAD
    public List<ItemDto> getAllByOwnerId(Long ownerId, Integer from, Integer size) {
        userRepository
                .findById(ownerId)
                .orElseThrow(() -> new NotFoundValidationException("Owner with id: " + ownerId + " not found"));
        log.info("Found owner (id:{}), return items.", ownerId);
        Pageable page = PageRequest.of(from / size, size);
        List<ItemDto> itemsList = itemMapper.toDtoList(itemRepository.findByOwnerId(ownerId, page));
        for (ItemDto itemDto : itemsList) {
            addBooking(itemDto);
            addComment(itemDto);
        }
        return itemsList;
=======
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        List<ItemDto> itemDtos = itemRepository.findAll()
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        for (ItemDto itemDto : itemDtos) {
            addBooking(itemDto);
            addComment(itemDto);
        }
        return itemDtos;
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
    }

    @Transactional(readOnly = true)
    public ItemDto getItem(Long id, Long requesterId) {
        ItemDto itemDto;
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new NotFoundValidationException("Item with id: " + id + " not found"));
        itemDto = itemMapper.toItemDto(item);
        addComment(itemDto);
        if (!item.getOwner().getId().equals(requesterId)) {
            return itemDto;
        }
        return addBooking(itemDto);
    }

    @Transactional(readOnly = true)
    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            log.debug("Searching: {}", text);
            Pageable page = PageRequest.of(from / size, size);
            return itemMapper.toDtoList(itemRepository.search(text, page).getContent());
        }
    }

    @Transactional
    public void removeItem(Long id) {
       itemRepository
               .findById(id)
<<<<<<< HEAD
               .orElseThrow(() -> new NotFoundValidationException("User with id: " + id + " not found"));
=======
               .orElseThrow(() -> new NotFoundValidationException("User with id: " + id + "not found"));
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
       itemRepository.deleteById(id);
    }

    @Transactional
    public CommentDto createComment(Long itemId, CommentDto commentDto, Long authorId) {
        User author = userRepository.findById(authorId).orElseThrow(() ->
                new NotFoundValidationException("Author not found."));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundValidationException("Item not found."));
        commentDto.setItemId(itemId);
        createCommentValidator(commentDto, authorId);
        commentDto.setAuthorName(author.getName());
        Comment comment = commentMapper.toComment(commentDto);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void createCommentValidator(CommentDto commentDto, Long authorId) {
        if (checkCommentTruth(commentDto.getItemId(), authorId)) {
            throw new IllegalRequestException("Comment not truly.");
        }
    }

    private Item itemOwnerNameDescAvailValidator(Item item, Item oldItem) {
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        return oldItem;
    }

    private ItemDto addBooking(ItemDto item) {
        LocalDateTime moment = LocalDateTime.now();
        Optional<Booking> bookingBefore = bookingRepository.findByItemIdAndEndIsBefore(item.getId(), moment);
        Optional<Booking> bookingAfter = bookingRepository.findByItemIdAndStartIsAfter(item.getId(), moment);
        bookingBefore.ifPresent(booking -> item.setLastBooking(bookingMapper.toDtoShort(booking)));
        bookingAfter.ifPresent(booking -> item.setNextBooking(bookingMapper.toDtoShort(booking)));
        return item;
    }

    private void addComment(ItemDto item) {
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());
        item.setComments(commentMapper.toDtoList(comments));
    }

    private boolean checkCommentTruth(Long itemId, Long authorId) {
        List<Booking> allBookings = bookingRepository.findAllByBookerIdAndItemIdAndEndIsBefore(authorId,
                itemId, LocalDateTime.now());
        allBookings = allBookings.stream().filter(b -> b.getStatus().equals(Status.APPROVED))
                .collect(Collectors.toList());
        return allBookings.isEmpty();
    }
}
