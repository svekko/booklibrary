package ee.smit.booklibrary.model;

import ee.smit.booklibrary.dto.BookResponseDto;
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

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "added_by_id")
    private UserAccount addedBy;

    public BookResponseDto toBookResponseDto() {
        return BookResponseDto.builder()
            .id(id)
            .title(title)
            .build();
    }
}
