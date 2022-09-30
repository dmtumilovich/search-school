package com.einott.search;

import com.einott.search.conf.ElasticsearchConfig;
import com.einott.search.domain.Movie;
import com.einott.search.service.MovieLensSearchServiceApplicationJoin;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import static com.einott.search.AppConstants.ES_CONFIG_PROPERTIES;

// TODO add commands
@Slf4j
public final class App {
    public static void main(String[] args) throws IOException {
        ElasticsearchConfig esConfig = ElasticsearchConfig.fromPropertiesFile(ES_CONFIG_PROPERTIES);
        MovieLensSearchServiceApplicationJoin movieLensService = new MovieLensSearchServiceApplicationJoin(esConfig);
        List<Movie> result = movieLensService.moviesForUserPut5Rating(1L);
        log.info("Result: {}", result);

        movieLensService.close();
    }
}
