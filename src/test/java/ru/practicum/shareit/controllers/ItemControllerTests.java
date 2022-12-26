package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService service;
    @Autowired
    private MockMvc mvc;
    ItemMapper itemMapper = new ItemMapperImpl();
    LocalDateTime moment = LocalDateTime.now();

    private final CreateItemRequest itemRequest1 =
            new CreateItemRequest(1L, "itemReq", "itemReqDisc", Boolean.TRUE, null);
    private final ItemDto itemDto1 = new ItemDto(1L, "itemReq", "itemReqDisc", Boolean.TRUE,
            null, null, null, null, null);
    private final ItemDto itemDto2 = new ItemDto(2L, "itemReq", "itemReqDisc", Boolean.TRUE,
            null, null, null, null, null);
    private final CommentDto commentDto1 =
            new CommentDto(1L, "comment", 2L, "author", moment);


    @Test
    void testCheckMapper() {
        assertEquals(itemDto1, requestToItemDto(itemRequest1));
    }

    @Test
    void testCreateItem() throws Exception {
        when(service.createItem(Mockito.any(CreateItemRequest.class), Mockito.anyLong()))
                .thenReturn(requestToItemDto(itemRequest1));
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemRequest1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemRequest1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemRequest1.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(Boolean.TRUE), Boolean.class));
    }

    @Test
    void testDeleteItem() throws Exception {
        doNothing().when(service).removeItem(1L);
        mvc.perform(delete("/items/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateItemNameIsBlank() throws Exception {
        itemRequest1.setName("   ");
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void testCreateItemDescriptionIsBlank() throws Exception {
        itemRequest1.setDescription("   ");
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void testCreateItemAvailableIsNull() throws Exception {
        itemRequest1.setAvailable(null);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void testPatchItem() throws Exception {
        ItemDto expected = requestToItemDto(itemRequest1);
        itemRequest1.setId(1L);
        when(service.updateItem(Mockito.any(CreateItemRequest.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(expected);
        mvc.perform(patch("/items/3")
                        .content(mapper.writeValueAsString(itemRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expected.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(expected.getName()), String.class))
                .andExpect(jsonPath("$.description", is(expected.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(expected.getAvailable()), Boolean.class));
    }

    @Test
    void testPatchItemNotOwner() throws Exception {
        when(service.updateItem(Mockito.any(CreateItemRequest.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(NotFoundValidationException.class);
        mvc.perform(patch("/items/3")
                        .content(mapper.writeValueAsString(commentDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void testGetItem() throws Exception {
        when(service.getItem(eq(2L), Mockito.anyLong()))
                .thenReturn(itemDto2);
        mvc.perform(get("/items/2")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2), Integer.class));
    }

    @Test
    void testGetItemNotFound() throws Exception {
        when(service.getItem(eq(5L), Mockito.anyLong()))
                .thenThrow(NotFoundValidationException.class);
        mvc.perform(get("/items/5")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void testGetAll() throws Exception {
        when(service.getAllByOwnerId(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(addToItemDtoList());
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetAllSizeIsNull() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("size", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void testGetAllSizeIsNegative() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("size", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void testGetAllFromIsNegative() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void testGetAllSizeAndFromIsNegative() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("size", "-1")
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void testSearch() throws Exception {
        when(service.search(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(addToItemDtoList());
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "ok")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testSearchTextBlank() throws Exception {
        when(service.search(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(new ArrayList<>());
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", " ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testSearchSizeIsNull() throws Exception {
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "ok")
                        .param("size", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void testSearchSizeIsNegative() throws Exception {
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "ok")
                        .param("size", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void testSearchFromIsNegative() throws Exception {
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "ok")
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }



    @Test
    void testSearchSizeAndFromIsNegative() throws Exception {
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "ok")
                        .param("size", "-1")
                        .param("from", "-1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void testCreateComment() throws Exception {
        when(service.createComment(Mockito.anyLong(), Mockito.any(CommentDto.class), Mockito.anyLong()))
                .thenReturn(commentDto1);
        commentDto1.setId(null);
        mvc.perform(post("/items/2/comment")
                        .content(mapper.writeValueAsString(commentDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto1.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto1.getText()), String.class))
                .andExpect(jsonPath("$.itemId", is(commentDto1.getItemId()), Long.class));
    }

    @Test
    void testCreateCommentTextIsBlank() throws Exception {
        commentDto1.setId(null);
        commentDto1.setText("            ");
        mvc.perform(post("/items/2/comment")
                        .content(mapper.writeValueAsString(commentDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void testCreateNotTrulyComment() throws Exception {
        when(service.createComment(Mockito.anyLong(), Mockito.any(CommentDto.class), Mockito.anyLong()))
                .thenThrow(NotFoundValidationException.class);
        commentDto1.setId(null);
        mvc.perform(post("/items/2/comment")
                        .content(mapper.writeValueAsString(commentDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    private List<ItemDto> addToItemDtoList() {
        List<ItemDto> itemDtos = new ArrayList<>();
        itemDtos.add(itemDto1);
        itemDtos.add(itemDto2);
        return itemDtos;
    }

    private ItemDto requestToItemDto(CreateItemRequest request) {
        return itemMapper.toItemDto(itemMapper.toItem(request));
    }
}
