package ee.svekko.booklibrary.exception;

import ee.svekko.booklibrary.error.InvalidDataError;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(InvalidDataError error) {
        super(error.getMessage());
    }
}
