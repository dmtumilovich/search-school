package com.einott.search.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Movie {
    private Long movieId;
    private String title;
    private List<String> genres;
}
