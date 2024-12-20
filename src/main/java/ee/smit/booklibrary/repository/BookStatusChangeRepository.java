package ee.smit.booklibrary.repository;

import ee.smit.booklibrary.model.BookStatusChange;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookStatusChangeRepository extends CrudRepository<BookStatusChange, Integer> {
    @Query(
        nativeQuery = true,
        value = """
            SELECT *
            FROM book_status_change
            WHERE book_id = :bookId
            AND valid_from <= now()
            AND valid_to > now()
            """
    )
    Optional<BookStatusChange> findValidBookStatusChangeByBookId(int bookId);
}
