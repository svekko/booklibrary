package ee.svekko.booklibrary.repository;

import ee.svekko.booklibrary.dto.BookResponseDto;
import ee.svekko.booklibrary.model.BookChange;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookChangeRepository extends CrudRepository<BookChange, Integer> {
    @Query(
        nativeQuery = true,
        value = """
            SELECT *
            FROM book_change
            WHERE book_id = :bookId
            AND valid_from <= now()
            AND valid_to > now()
            """
    )
    Optional<BookChange> getBookByBookId(int bookId);

    @Query(
        nativeQuery = true,
        value = """
            SELECT book_change.*,
                   book_status.id AS status_id,
                   book_status.name AS status_name,
                   book_status_change.book_used_by_id AS book_used_by_id
            FROM book_change
            LEFT JOIN book_status_change ON (
                book_status_change.book_id = book_change.book_id
                AND book_status_change.valid_from <= now()
                AND book_status_change.valid_to > now()
            )
            LEFT JOIN book_status ON (
                book_status.id = book_status_change.book_status_id
            )
            WHERE book_change.valid_from <= now()
            AND book_change.valid_to > now()
            """
    )
    List<BookResponseDto> getBooks();
}
