package ee.svekko.booklibrary.repository;

import ee.svekko.booklibrary.dto.BookResponseDto;
import ee.svekko.booklibrary.model.BookChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookChangeRepository extends JpaRepository<BookChange, Integer> {
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
            WHERE trim(upper(title)) = trim(upper(:title))
            AND valid_from <= now()
            AND valid_to > now()
            """
    )
    Optional<BookChange> getBookByTitle(String title);

    @Query(
        nativeQuery = true,
        value = """
            SELECT book_change.book_id AS id,
                   book_change.title AS title,
                   book_status.id AS status_id,
                   book_status.name AS status_name,
                   book_used_by.id AS book_used_by_id,
                   book_used_by.email AS book_used_by_email,
                   book_change.created_by_id AS created_by_id,
                   book_status_change.valid_to AS status_valid_to
            FROM book_change
            LEFT JOIN book_status_change ON (
                book_status_change.book_id = book_change.book_id
                AND book_status_change.valid_from <= now()
                AND book_status_change.valid_to > now()
            )
            LEFT JOIN book_status ON (
                book_status.id = book_status_change.book_status_id
            )
            LEFT JOIN user_account AS book_used_by ON (
                book_used_by.id = book_status_change.book_used_by_id
            )
            WHERE book_change.valid_from <= now()
            AND book_change.valid_to > now()
            AND book_change.title ILIKE :title
            ORDER BY book_change.valid_from DESC
            """
    )
    List<BookResponseDto> getBooks(String title);
}
