package org.example.bookstore.exception;

import java.util.Collection;

public class BookstoreEntityNotFoundException extends BookstoreRuntimeException {

    private static final String ID_MESSAGE_TEMPLATE = "Entity %s with id '%s' has not been found in the system";
    private static final String MULTIPLE_IDS_MESSAGE_TEMPLATE =
        "Entities %s with ids '%s' has not been found in the system";

    public BookstoreEntityNotFoundException(Long id, Class<?> clazz) {
        super(String.format(ID_MESSAGE_TEMPLATE, clazz.getSimpleName(), id));
    }

    public BookstoreEntityNotFoundException(Collection<Long> ids, Class<?> clazz) {
        super(String.format(MULTIPLE_IDS_MESSAGE_TEMPLATE, clazz.getSimpleName(), ids));
    }
}
