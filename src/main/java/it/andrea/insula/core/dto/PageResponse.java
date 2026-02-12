package it.andrea.insula.core.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PageResponse<T>(
        List<T> content,
        PageMetadata page
) {

    public static <T> PageResponse<T> fromPage(Page<T> page) {
        PageMetadata pageMetadata = PageMetadata.builder()
                .currentPage(page.getNumber() + 1)
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();

        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(pageMetadata)
                .build();
    }

    @Builder
    public record PageMetadata(
            int currentPage,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) {
    }
}