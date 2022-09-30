package com.einott.search.domain.movielens;

import lombok.Data;

@Data
public class Tag {
    private Long userId;
    private Long movieId;
    private String tag;
    private Long timestamp;
}
