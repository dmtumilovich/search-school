package com.einott.search.domain.movielens;

import lombok.Data;

@Data
public class Link {
    private Long movieId;
    private String imdbId;
    private String tmdbId;
}
