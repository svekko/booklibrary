package ee.smit.booklibrary.model;

import ee.smit.booklibrary.dto.BookResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "book_change")
public class BookChange {
    @Id
    @SequenceGenerator(name = "book_change_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "book_change_id_seq", strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "changed_by_id")
    private UserAccount changedBy;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    public BookResponseDto toBookResponseDto() {
        return BookResponseDto.builder()
            .id(book.getId())
            .title(title)
            .build();
    }
}
