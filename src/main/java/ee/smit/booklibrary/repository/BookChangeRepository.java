package ee.smit.booklibrary.repository;

import ee.smit.booklibrary.model.BookChange;
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
            SELECT *
            FROM book_change
            WHERE valid_from <= now()
            AND valid_to > now()
            """
    )
    List<BookChange> getBooks();

    @Query(
        nativeQuery = true,
        value = """
            SELECT *
            FROM book_change
            WHERE changed_by_id = :changedById
            AND valid_from <= now()
            AND valid_to > now()
            """
    )
    List<BookChange> getBooksWhereChangedBy(int changedById);
}
