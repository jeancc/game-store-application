package com.dynacode.store.common;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {


    private List<T> content;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
    private boolean isFirst;

}
