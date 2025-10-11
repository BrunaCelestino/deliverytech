package com.deliverytech.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PageResponse<T> {
    List<T> data;
    Integer page;
    Integer pageSize;
    Integer totalPages;
    Long totalElements;


    public PageResponse(List<T> data, 
                        Long totalElements, 
                        Integer totalPages, 
                        Integer pageSize, 
                        Integer page) {
        this.data = data;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.pageSize = pageSize;
        this.page = page;
    }

}
