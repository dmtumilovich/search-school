package com.einott.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.einott.search.conf.ElasticsearchConfig;
import com.einott.search.domain.RedditRecord;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class RedditSearchService implements Closeable {

    private final ElasticsearchClient esClient;
    private final String index;

    public RedditSearchService(ElasticsearchConfig esConfig, String index) {
        this.esClient = new ElasticsearchClient(new RestClientTransport(esConfig.restClient(), esConfig.jacksonJsonpMapper()));
        this.index = index;
    }


    public List<Hit<RedditRecord>> matchBody(String searchText) {
        log.debug("Match body query [searchText={}]", searchText);
        Query byBody = MatchQuery.of(m -> m
                .field("body")
                .query(searchText))
            ._toQuery();
        return query(byBody);
    }

    public List<Hit<RedditRecord>> matchPhraseBody(String searchText) {
        log.debug("Match phrase body query [searchText={}]", searchText);
        Query byBody = MatchPhraseQuery.of(m -> m
                .field("body")
                .query(searchText))
            ._toQuery();
        return query(byBody);
    }

    public List<Hit<RedditRecord>> matchTitleAndBody(String searchTitleText, String searchBodyText) {
        log.debug("Match title and body query [searchTitleText={}, searchBodyText={}]", searchTitleText, searchBodyText);
        Query byTitle = MatchQuery.of(m -> m
                .field("title")
                .query(searchTitleText))
            ._toQuery();
        Query byBody = MatchQuery.of(m -> m
                .field("body")
                .query(searchBodyText))
            ._toQuery();
        Query combinedQuery = BoolQuery.of(b -> b
                .must(byTitle)
                .must(byBody))
            ._toQuery();
        return query(combinedQuery);
    }

    public List<Hit<RedditRecord>> matchBodyBoostByScore(String searchText, Integer boostScore) {
        log.debug("Match body query, boost relevance score if post score >= boostScore [searchText={}, boostScore={}]", searchText, boostScore);
        Query byBody = MatchQuery.of(m -> m
                .field("body")
                .query(searchText))
            ._toQuery();
        Query rangeScore = RangeQuery.of(r -> r
                .field("score")
                .gte(JsonData.of(boostScore))
                .boost(1.2f))
            ._toQuery();
        Query combinedQuery = BoolQuery.of(b -> b
                .must(byBody)
                .should(rangeScore))
            ._toQuery();
        return query(combinedQuery);
    }

    public List<Hit<RedditRecord>> matchBodyBoostByDate(String searchText) {
        log.debug("Match body query, boost relevance score by post timestamp [searchText={}]", searchText);
        Query byBody = MatchQuery.of(m -> m
                .field("body")
                .query(searchText))
            ._toQuery();
        Query boostByDate = DistanceFeatureQuery.of(d -> d
                .field("timestamp")
                .pivot(JsonData.of("183d"))
                .origin(JsonData.of(LocalDateTime.now().toString())))
            ._toQuery();
        Query combinedQuery = BoolQuery.of(b -> b
                .must(byBody)
                .should(boostByDate))
            ._toQuery();
        return query(combinedQuery);
    }

    public List<Hit<RedditRecord>> postsBetweenDates(LocalDateTime startDt, LocalDateTime endDt) {
        log.debug("Posts between dates [startDt={}, endDt={}]", startDt, endDt);
        Query betweenDates = RangeQuery.of(r -> r
                .field("timestamp")
                .gte(JsonData.of(startDt.toString()))
                .lte(JsonData.of(endDt.toString())))
            ._toQuery();
        return query(betweenDates);
    }

    public List<Hit<RedditRecord>> postsForUserId(String userId) {
        log.debug("Posts for userId [userId={}]", userId);
        Query byUserId = TermQuery.of(t -> t
                .field("id")
                .value(userId))
            ._toQuery();
        return query(byUserId);
    }

    private List<Hit<RedditRecord>> query(Query query) {
        SearchResponse<RedditRecord> response = null;
        try {
             response = esClient.search(s -> s
                .index(this.index)
                .query(query), RedditRecord.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public void close() throws IOException {
        if (esClient != null) {
            esClient._transport().close();
        }
    }
}
