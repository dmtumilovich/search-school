package com.einott.search;

import com.einott.search.conf.ElasticsearchConfig;
import com.einott.search.domain.movielens.Movie;
import com.einott.search.domain.movielens.MovieLens;
import com.einott.search.domain.movielens.Rating;
import com.einott.search.domain.movielens.Tag;
import com.einott.search.service.IndexService;
import com.einott.search.util.CsvReader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * For simplicity this movie-lens logic is added to the indexing module.
 * This app reads CSV content, joins it to index denormalized data.
 */
@Slf4j
public class AppMovieLens {

    private static final String MOVIE_LENS_INDEX = "movie-lens-denormalized";
//    private static final String MOVIE_LENS_INDEX = "movie-lens-nested";

    public static void main(String[] args) throws IOException {
        IndexService indexService = null;
        try {
            ElasticsearchConfig esConfig = ElasticsearchConfig.builder()
                    .hostname(args[0])
                    .port(Integer.parseInt(args[1]))
                    .scheme(args[2])
                    .username(args[3])
                    .password(args[4])
                    .build();
            indexService = new IndexService(esConfig);
            Stream<Movie> movies = CsvReader.readCsvLazy(args[5], Movie.class);
            Map<Long, List<Tag>> tags = CsvReader.readCsvIndexed(args[6], Tag.class, Tag::getMovieId);
            Map<Long, List<Rating>> ratings = CsvReader.readCsvIndexed(args[7], Rating.class, Rating::getMovieId);

            // here we denormalize data
            Stream<MovieLens> movieLensStream = MovieLensMapper.map(movies, tags, ratings);
            indexService.bulkIndex(MOVIE_LENS_INDEX, movieLensStream);

        } catch (Throwable e) {
            log.error("Error while processing", e);
        } finally {
            indexService.close();
        }
    }

}
