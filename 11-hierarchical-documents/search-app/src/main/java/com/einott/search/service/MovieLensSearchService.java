package com.einott.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import com.einott.search.domain.Movie;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class MovieLensSearchService implements Closeable {

    protected ElasticsearchClient esClient;

    public abstract List<Movie> moviesByTag(String tag);
    public abstract Map<Movie, Double> moviesSortByAverageRating(int limit);
    public abstract Map<Movie, List<String>> top10TagsForMovie(String movieToSearch);
    public abstract List<Movie> moviesForUserPut5Rating(Long userId);

    @Override
    public void close() throws IOException {
        if (esClient != null) {
            esClient._transport().close();
        }
    }

    protected <T> List<T> toEntityList(List<Hit<T>> hits) {
        return hits.stream().map(Hit::source).collect(Collectors.toList());
    }

    protected <T> List<Hit<T>> query(Query query, Class<T> pojo, String index) {
        SearchResponse<T> response = query(query, null, pojo, index);
        if (response == null) {
            log.warn("Received empty response!");
            return Collections.emptyList();
        }
        TotalHits total = response.hits().total();
        if (total.relation() == TotalHitsRelation.Eq) {
            log.debug("There are {} results", total.value());
        } else {
            log.debug("There are more than {} results", total.value());
        }
        return response.hits().hits();
    }

    protected <T> SearchResponse<T> query(Query query, Map<String, Aggregation> aggregations, Class<T> pojo, String index) {
        SearchResponse<T> response = null;
        try {
            response = esClient.search(s -> {
                s.index(index);
                if (query != null) s.query(query);
                if (aggregations != null) s.aggregations(aggregations);
                return s;
            }, pojo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

}
