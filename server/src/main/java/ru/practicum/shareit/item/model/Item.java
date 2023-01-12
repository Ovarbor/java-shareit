package ru.practicum.shareit.item.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   @Column(name = "name", nullable = false)
   private String name;
   @Column(name = "description", nullable = false)
   private String description;
   @Column(name = "is_available", nullable = false)
   private Boolean available;
   @ManyToOne
   @JoinColumn(name = "owner_id", referencedColumnName = "id")
   private User owner;
   @Column(name = "request_id")
   private Long requestId;

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
      Item item = (Item) o;
      return id != null && Objects.equals(id, item.id);
   }

   @Override
   public int hashCode() {
      return getClass().hashCode();
   }
}
