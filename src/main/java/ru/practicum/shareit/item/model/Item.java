package ru.practicum.shareit.item.model;
<<<<<<< HEAD
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
=======
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

   private Long id;
   @NotBlank
   private String name;
   @NotBlank
   private String description;
   @NotNull
   private Boolean available;
   private User owner;
   private ItemRequest request;
>>>>>>> 4f16f1bf88eed9c7fa247ad0c502c2e149be4d77

   public Item(Item newItem) {
      this.setId(newItem.getId());
      this.setName(newItem.getName());
      this.setDescription(newItem.getDescription());
      this.setAvailable(newItem.getAvailable());
      this.setOwner(newItem.getOwner());
<<<<<<< HEAD
      this.setRequestId(newItem.getRequestId());
   }

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
=======
      this.setRequest(newItem.getRequest());
>>>>>>> 4f16f1bf88eed9c7fa247ad0c502c2e149be4d77
   }
}
