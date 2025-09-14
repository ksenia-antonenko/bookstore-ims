package org.example.bookstore.util;

import java.time.Clock;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final ZoneId DEFAULT_TIME_ZONE = Clock.systemUTC().getZone();

    public static final String DEFAULT_USER_NAME = "system";

    public static int PAGE_MAX_SIZE = 100;
}
