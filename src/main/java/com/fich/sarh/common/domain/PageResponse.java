package com.fich.sarh.common.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast
) implements Serializable {

    public static <T> PageResponse<T> of(List<T> content, int pageNumber, int pageSize, long totalElements) {
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
        boolean isFirst = pageNumber <= 0;
        boolean isLast = totalPages == 0 || pageNumber >= totalPages - 1;
        List<T> safeContent = content != null ? content : Collections.emptyList();
        return new PageResponse<>(safeContent, pageNumber, pageSize, totalElements, totalPages, isFirst, isLast);
    }

    public <U> PageResponse<U> map(Function<? super T, U> converter) {
        List<U> mappedContent = this.content.stream().map(converter).toList();
        return new PageResponse<>(mappedContent, pageNumber, pageSize, totalElements, totalPages, isFirst, isLast);
    }
}
