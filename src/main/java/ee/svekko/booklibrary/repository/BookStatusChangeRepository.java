package ee.svekko.booklibrary.repository;

import ee.svekko.booklibrary.model.BookStatusChange;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookStatusChangeRepository extends CrudRepository<BookStatusChange, Integer> {
    @Query(
        nativeQuery = true,
        value = """
            SELECT book_status_change.*
            FROM book_status_change
            JOIN book_change ON (
                book_change.book_id = book_status_change.book_id
                AND book_change.valid_from <= now()
                AND book_change.valid_to > now()
            )
            WHERE book_status_change.book_id = :bookId
            AND book_status_change.valid_from <= now()
            AND book_status_change.valid_to > now()
            """
    )
    Optional<BookStatusChange> findValidBookStatusChangeByBookId(int bookId);
}
