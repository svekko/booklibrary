package ee.smit.booklibrary.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BookStatusValue {
    NOT_IN_USE(1),
    RESERVED(2),
    BORROWED(3);

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
