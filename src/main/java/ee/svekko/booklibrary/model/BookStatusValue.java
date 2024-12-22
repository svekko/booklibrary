package ee.svekko.booklibrary.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BookStatusValue {
    RESERVED(1),
    BORROWED(2);

    private final int id;

    public static BookStatusValue fromInt(int id) {
        for (BookStatusValue value : values()) {
            if (value.id == id) {
                return value;
            }
        }

        throw new IllegalArgumentException("BookStatusValue not found for id: " + id);
    }

    public boolean equals(BookStatus bookStatus) {
        return this == bookStatus.getValue();
    }
}
