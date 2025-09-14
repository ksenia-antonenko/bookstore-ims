package org.example.bookstore.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class ApiError {
    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<String> details;
}
