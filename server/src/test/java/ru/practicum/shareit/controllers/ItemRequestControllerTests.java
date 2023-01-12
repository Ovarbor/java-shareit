package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestMapperImpl;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService service;
    @Autowired
    private MockMvc mvc;
    ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();
    LocalDateTime moment = LocalDateTime.now();

    private final ItemRequest itemRequest1 =
            new ItemRequest(1L, "desc", null, moment.plusDays(1L));
    private final ItemRequestDto itemRequestDto1 =
            new ItemRequestDto(1L, "desc", moment.plusDays(1), new ArrayList<>());
    private final ItemRequestDto itemRequestDto2 =
            new ItemRequestDto(2L, "desc2", moment.plusDays(2), new ArrayList<>());


    @Test
    void testCheckMapper() {
        assertEquals(itemRequestMapper.toItemRequestDto(itemRequest1), requestToDto(itemRequestMapper.toItemRequestDto(itemRequest1)));
    }

    @Test
    void testCreateItemRequest() throws Exception {
        when(service.create(Mockito.any(ItemRequestDto.class), Mockito.anyLong())).thenReturn(itemRequestDto1);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription()), String.class))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void testGetByUser() throws Exception {
        when(service.getByOwner(1L)).thenReturn(addToDtoList());
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void testGetAll() throws Exception {
        when(service.getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(addToDtoList());
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void testGetById() throws Exception {
        when(service.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemRequestDto1);
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    private List<ItemRequestDto> addToDtoList() {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        itemRequestDtos.add(itemRequestDto1);
        itemRequestDtos.add(itemRequestDto2);
        return itemRequestDtos;
    }

    private ItemRequestDto requestToDto(ItemRequestDto request) {
        return itemRequestMapper.toItemRequestDto(itemRequestMapper.toItemRequest(request));
    }
}
