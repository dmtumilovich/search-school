package com.einott.search.domain;

import lombok.Data;

@Data
public class Rating {
    private Long userId;
    private Long movieId;
    private Integer rating;
    private Long timestamp;
}
