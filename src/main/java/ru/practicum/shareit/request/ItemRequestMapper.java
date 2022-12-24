package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto toItemRequestDto(ItemRequest request);

    List<ItemRequestDto> toListDto(List<ItemRequest> requests);
}
