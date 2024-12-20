package ee.smit.booklibrary.repository;

import ee.smit.booklibrary.model.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {
    @Query(
        nativeQuery = true,
        value = """
            SELECT book.*
            FROM book_status_change
            JOIN book ON (book.id = book_status_change.book_id)
            WHERE book_id = :bookId
            AND valid_from <= now()
            AND valid_to > now()
            """
    )
    Optional<Book> findValidBookByBookId(int bookId);

    @Query(
        nativeQuery = true,
        value = """
            SELECT book.*
            FROM book_status_change
            JOIN book ON (book.id = book_status_change.book_id)
            WHERE valid_from <= now()
            AND valid_to > now()
            """
    )
    List<Book> getBooks();

    @Query(
        nativeQuery = true,
        value = """
            SELECT book.*
            FROM book_status_change
            JOIN book ON (book.id = book_status_change.book_id)
            WHERE added_by_id = :addedById
            AND valid_from <= now()
            AND valid_to > now()
            """
    )
    List<Book> getBooksWhereAddedBy(int addedById);
}
