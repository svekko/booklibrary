package ee.smit.booklibrary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "book_status_change")
public class BookStatusChange {
    @Id
    @SequenceGenerator(name = "book_status_change_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "book_status_change_id_seq", strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "book_status_id")
    private BookStatus bookStatus;

    @ManyToOne
    @JoinColumn(name = "book_used_by_id")
    private UserAccount bookUsedBy;

    @ManyToOne
    @JoinColumn(name = "changed_by_id")
    private UserAccount changedBy;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;
}
