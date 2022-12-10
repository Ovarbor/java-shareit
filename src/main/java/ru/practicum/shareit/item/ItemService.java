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
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Optional<User> owner = userRepository.findById(userId);
        if(owner.isEmpty()) throw new NotFoundValidationException("Owner with id: " + userId + " not found");
        Item newItem = itemMapper.toItem(itemDto);
        newItem.setOwner(owner.get());
        log.info("Item created" + newItem);
        return itemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Transactional
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Optional<Item> oldItem = itemRepository.findById(itemId);
        if (oldItem.isEmpty()) throw new NotFoundValidationException("Item with id: " + itemId + " not found");
        if (!Objects.equals(oldItem.get().getOwner().getId(), userId))
            throw new NotFoundValidationException("User not owner");
        Item item  = itemMapper.toItem(itemDto);
        Item itemNew  = itemOwnerNameDescAvailValidator(item, oldItem.get());
        log.info("Item updated" + itemNew);
        return itemMapper.toItemDto(itemNew);
    }

    @Transactional(readOnly = true)
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
    }

    @Transactional(readOnly = true)
    public ItemDto getItem(Long id, Long requesterId) {
        ItemDto itemDto;
        Optional<Item> item  = itemRepository.findById(id);
        if (item.isPresent()) {
            itemDto = itemMapper.toItemDto(item.get());
            addComment(itemDto);
            if (!item.get().getOwner().getId().equals(requesterId)) {
                return itemDto;
            }
        } else {
            throw new NotFoundValidationException("Item with id: " + id + " not found");
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
        Optional<Item> item = itemRepository.findById(id);
        if (item.isPresent()) {
            itemRepository.deleteById(id);
        } else {
            throw new NotFoundValidationException("User with id: " + id + "not found");
        }
    }

    @Transactional
    public CommentDto createComment(Long itemId, CommentDto commentDto, Long authorId) {
        Optional<User> author = userRepository.findById(authorId);
        Optional<Item> item = itemRepository.findById(itemId);
        commentDto.setItemId(itemId);
        if (author.isEmpty()) throw new NotFoundValidationException("Author not found.");
        if (item.isEmpty()) throw new NotFoundValidationException("Item not found.");
        createCommentValidator(commentDto, authorId);
        commentDto.setAuthorName(author.get().getName());
        Comment comment = commentMapper.toComment(commentDto);
        comment.setAuthor(author.get());
        comment.setItem(item.get());
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
        item.setLastBooking(bookingMapper.toDtoShort(bookingRepository.findByItemIdAndEndIsBefore(item.getId(),
                moment)));
        item.setNextBooking(bookingMapper.toDtoShort(bookingRepository.findByItemIdAndStartIsAfter(item.getId(),
                moment)));
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
