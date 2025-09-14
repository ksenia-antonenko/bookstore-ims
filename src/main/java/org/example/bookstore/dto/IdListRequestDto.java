package org.example.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record IdListRequestDto(List<@NotNull Long> ids) {}
