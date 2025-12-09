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
    private int pageNumber; //número de página
    private int size;//tamaño de la página

    private long totalElements;
    private int totalPages;
    private boolean isLast;
    private boolean isFirst;

}
