package org.example.bookstore.dto;

import java.util.List;

public record SearchResponseDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
