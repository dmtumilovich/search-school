package com.einott.search.domain.movielens;

import lombok.Data;

@Data
public class Rating {
    private Long userId;
    private Long movieId;
    private Double rating;
    private Long timestamp;
}
