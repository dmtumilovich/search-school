package com.einott.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.einott.search.conf.ElasticsearchConfig;
import com.einott.search.domain.Movie;
import com.einott.search.domain.MovieLens;
import com.einott.search.mapper.MovieMapper;

import java.util.List;
import java.util.Map;

import static com.einott.search.AppConstants.INDEX_MOVIE_LENS_DENORMALIZED;

public class MovieLensSearchServiceNested extends MovieLensSearchService{

    public MovieLensSearchServiceNested(ElasticsearchConfig esConfig) {
        this.esClient = new ElasticsearchClient(new RestClientTransport(esConfig.restClient(), esConfig.jacksonJsonpMapper()));
    }

    @Override
    public List<Movie> moviesByTag(String tag) {
        Query tagsQuery = MatchQuery.of(m -> m
                .field("tag")
                .query(tag))
            ._toQuery();
        List<MovieLens> movieLens = toEntityList(query(tagsQuery, MovieLens.class, INDEX_MOVIE_LENS_DENORMALIZED));
        return MovieMapper.map(movieLens);
    }

    @Override
    public Map<Movie, Double> moviesSortByAverageRating(int limit) {
        return null;
    }

    @Override
    public Map<Movie, List<String>> top10TagsForMovie(String movieToSearch) {
        return null;
    }

    @Override
    public List<Movie> moviesForUserPut5Rating(Long userId) {
        return null;
    }
}
