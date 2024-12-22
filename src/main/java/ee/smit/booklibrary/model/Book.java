package ee.smit.booklibrary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "book")
public class Book {
    @Id
    @SequenceGenerator(name = "book_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "book_id_seq", strategy = GenerationType.AUTO)
    private Integer id;
}
