package ru.practicum.shareit.request.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private Long id;
    private String description;
    private Long requester;
    private LocalDateTime created;
}
