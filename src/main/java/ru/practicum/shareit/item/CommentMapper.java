package ru.practicum.shareit.item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "itemId", source = "item.id")
    CommentDto toCommentDto(Comment comment);

    Comment toComment(CommentDto commentDto);

    List<CommentDto> toDtoList(List<Comment> comments);
}
