package com.einott.search.mapper;

import com.einott.search.domain.Movie;
import com.einott.search.domain.MovieLens;

import java.util.List;
import java.util.stream.Collectors;

public class MovieMapper {

    public static List<Movie> map(List<MovieLens> movieLensList) {
        return movieLensList.stream().map(MovieMapper::map).collect(Collectors.toList());
    }

    public static Movie map(MovieLens movieLens) {
        return Movie.builder()
            .movieId(movieLens.getMovieId())
            .title(movieLens.getTitle())
            .genres(movieLens.getGenres())
            .build();
    }

}
