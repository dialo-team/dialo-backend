package com.fit.se.infrastructure.support.pagination;

import org.springframework.data.domain.Page;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PageMetadataFactory {

    private PageMetadataFactory() {
    }

    public static Map<String, Object> from(Page<?> page) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("page", page.getNumber());
        metadata.put("size", page.getSize());
        metadata.put("totalElements", page.getTotalElements());
        metadata.put("totalPages", page.getTotalPages());
        return metadata;
    }
}
