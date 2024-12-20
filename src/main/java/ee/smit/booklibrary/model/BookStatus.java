package ee.smit.booklibrary.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "book_status")
public class BookStatus {
    @Id
    private Integer id;

    @Column(name = "name")
    private String name;

    public BookStatusValue getValue() {
        return BookStatusValue.fromInt(id);
    }
}
