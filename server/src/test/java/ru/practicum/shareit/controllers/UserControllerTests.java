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
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    UserService service;

    private final UserDto userDto1 = new UserDto(1L, "user1", "email@email1.com");
    private final UserDto userDto2 = new UserDto(2L, "user2", "email@email2.com");
    private final UserDto userDto3 = new UserDto(3L, "user3", "email@email3.com");
    private final UserDtoUpdate userDtoUpdate1 = new UserDtoUpdate(0L, "userUpdate", "email@update");
    private final UserDtoUpdate userDtoUpdate2 = new UserDtoUpdate(2L, "userUpdate", "email@update");

    @Test
    void testCreateUser() throws Exception {
        when(service.createUser(Mockito.any(UserDto.class))).thenReturn(userDto1);
        userDto1.setId(2L);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2L), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail()), String.class));
    }


    @Test
    void testCreateUserEmailAlreadyExists() throws Exception {
        when(service.createUser(Mockito.any(UserDto.class))).thenThrow(NotFoundValidationException.class);
        userDto1.setId(null);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void testUpdateUser() throws Exception {
        userDtoUpdate1.setName("myNameIs");
        when(service.updateUser(1L,userDtoUpdate1)).thenReturn(userDtoUpdate1);
        mvc.perform(patch("/users/1").content(mapper.writeValueAsString(userDtoUpdate1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdate1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdate1.getName())));
    }

    @Test
    void testGetAll() throws Exception {
        when(service.getAllUsers()).thenReturn(addToUserDtoList());
        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void testGetById() throws Exception {
        when(service.getUser(Mockito.anyLong())).thenReturn(userDto1);
        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail()), String.class));
    }

    @Test
    void testDelete() throws Exception {
        mvc.perform(delete("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private List<UserDto> addToUserDtoList() {
        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(userDto1);
        userDtos.add(userDto2);
        userDtos.add(userDto3);
        return userDtos;
    }
}
