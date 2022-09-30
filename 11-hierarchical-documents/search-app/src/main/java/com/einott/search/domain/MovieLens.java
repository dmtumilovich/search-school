package com.einott.search.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MovieLens {
    private Long movieId;
    private String title;
    private List<String> genres;
    private List<Tag> tags;
    private List<Rating> ratings;

    @Data
    @AllArgsConstructor
    public static class Tag {
        private Long userId;
        private String tag;
    }

    @Data
    @AllArgsConstructor
    public static class Rating {
        private Long userId;
        private Double rating;
    }

}
