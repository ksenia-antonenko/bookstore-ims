package org.example.bookstore.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MdcKeys {

    public static final String USERNAME = "username";
    public static final String TRACE_ID = "traceId";
    public static final String REQUEST = "request";
}
