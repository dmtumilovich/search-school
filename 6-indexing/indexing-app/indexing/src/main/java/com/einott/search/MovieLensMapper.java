package com.einott.search;

import com.einott.search.domain.movielens.Movie;
import com.einott.search.domain.movielens.MovieLens;
import com.einott.search.domain.movielens.Rating;
import com.einott.search.domain.movielens.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieLensMapper {

    public static Stream<MovieLens> map(Stream<Movie> movies, Map<Long, List<Tag>> tags, Map<Long, List<Rating>> ratings) {
        return movies.map(m -> {
            List<MovieLens.Tag> movieTags = Optional.ofNullable(tags.get(m.getMovieId()))
                    .orElseGet(ArrayList::new)
                    .stream().map(t -> new MovieLens.Tag(t.getUserId(), t.getTag())).collect(Collectors.toList());
            List<MovieLens.Rating> movieRatings = Optional.ofNullable(ratings.get(m.getMovieId()))
                    .orElseGet(ArrayList::new)
                    .stream().map(r -> new MovieLens.Rating(r.getUserId(), r.getRating())).collect(Collectors.toList());
            return MovieLens.builder()
                    .movieId(m.getMovieId())
                    .title(m.getTitle())
                    .genres(m.getGenres())
                    .tags(movieTags)
                    .ratings(movieRatings)
                    .build();
        });
    }

}
