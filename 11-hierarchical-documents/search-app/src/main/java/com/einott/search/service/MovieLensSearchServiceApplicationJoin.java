package com.einott.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.einott.search.conf.ElasticsearchConfig;
import com.einott.search.domain.Movie;
import com.einott.search.domain.Rating;
import com.einott.search.domain.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.einott.search.AppConstants.*;

@Slf4j
public class MovieLensSearchServiceApplicationJoin extends MovieLensSearchService {

    public MovieLensSearchServiceApplicationJoin(ElasticsearchConfig esConfig) {
        this.esClient = new ElasticsearchClient(new RestClientTransport(esConfig.restClient(), esConfig.jacksonJsonpMapper()));
    }

    @Override
    public List<Movie> moviesByTag(String tag) {
        Query tagsQuery = MatchQuery.of(m -> m
                .field("tag")
                .query(tag))
            ._toQuery();
        List<Tag> tags = toEntityList(query(tagsQuery, Tag.class, INDEX_TAGS));
        Set<Long> movieIds = tags.stream().map(Tag::getMovieId).collect(Collectors.toSet());
        Query moviesQuery = TermsQuery.of(t -> t
                .field("movieId")
                .terms(b -> b.value(movieIds.stream().map(FieldValue::of).collect(Collectors.toList()))))
            ._toQuery();
        return toEntityList(query(moviesQuery, Movie.class, INDEX_MOVIES));
    }

    @Override
    public Map<Movie, Double> moviesSortByAverageRating(int limit) {
        // average rating aggregation
        TermsAggregation aggMovies = TermsAggregation.of(t -> t
            .field("movieId")
            .size(limit)
            .order(Map.of("agg-average-rating", SortOrder.Desc)));
        Aggregation aggAvgRating = AverageAggregation.of(a -> a
            .field("rating"))._toAggregation();
        Aggregation agg = new Aggregation.Builder()
            .terms(aggMovies)
            .aggregations("agg-average-rating", aggAvgRating)
            .build();

        SearchResponse<Void> response = query(null, Map.of("agg-movies", agg), Void.class, INDEX_RATINGS);
        // collecting movieId -> avgRating pairs
        Map<Long, Double> movieIdAvgRating = response.aggregations().get("agg-movies").lterms().buckets()
            .array().stream()
            .collect(Collectors.toMap(LongTermsBucket::key, b -> b.aggregations().get("agg-average-rating").avg().value()));

        // query movies
        Query moviesQuery = TermsQuery.of(t -> t
                .field("movieId")
                .terms(b -> b.value(movieIdAvgRating.keySet().stream().map(FieldValue::of).collect(Collectors.toList()))))
            ._toQuery();
        List<Movie> movies = toEntityList(query(moviesQuery, Movie.class, INDEX_MOVIES));

        // join
        return movies.stream()
            .collect(Collectors.toMap(m -> m, m -> movieIdAvgRating.get(m.getMovieId())));
    }

    @Override
    public Map<Movie, List<String>> top10TagsForMovie(String movieToSearch) {
        // query movies by its title
        Query moviesQuery = MatchQuery.of(m -> m
                .field("title")
                .query(movieToSearch))
            ._toQuery();
        List<Movie> movies = toEntityList(query(moviesQuery, Movie.class, INDEX_MOVIES));

        Map<Movie, List<String>> topTagsForMovie = new HashMap<>();
        for (Movie movie : movies) {
            Query movieIdQuery = TermQuery.of(t -> t
                    .field("movieId")
                    .value(movie.getMovieId()))
                ._toQuery();
            Aggregation aggTopTags = TermsAggregation.of(t -> t.field("tag.keyword").size(10))._toAggregation();
            SearchResponse<Void> response = query(movieIdQuery, Map.of("agg-tags", aggTopTags), Void.class, INDEX_TAGS);
            List<String> top10Tags = response.aggregations().get("agg-tags").sterms().buckets().array().stream().map(StringTermsBucket::key).collect(Collectors.toList());
            topTagsForMovie.put(movie, top10Tags);
        }

        return topTagsForMovie;
    }

    @Override
    public List<Movie> moviesForUserPut5Rating(Long userId) {
        Query userIdQuery = TermQuery.of(t -> t
                .field("userId")
                .value(userId))
            ._toQuery();
        Query ratingQuery = TermQuery.of(t -> t
                .field("rating")
                .value(5.0))
            ._toQuery();
        Query boolQuery = BoolQuery.of(b -> b
            .must(userIdQuery, ratingQuery))._toQuery();
        List<Rating> ratings = toEntityList(query(boolQuery, Rating.class, INDEX_RATINGS));
        Set<Long> movieIds = ratings.stream().map(Rating::getMovieId).collect(Collectors.toSet());
        Query moviesQuery = TermsQuery.of(t -> t
                .field("movieId")
                .terms(b -> b.value(movieIds.stream().map(FieldValue::of).collect(Collectors.toList()))))
            ._toQuery();
        return toEntityList(query(moviesQuery, Movie.class, INDEX_MOVIES));
    }

}
