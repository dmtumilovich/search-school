package com.einott.search.service;

import java.util.List;
import java.util.stream.Stream;

import com.einott.search.conf.ElasticsearchConfig;
import com.google.common.collect.Iterators;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IndexService {

    private final ElasticsearchClient esClient;

    private static final int BULK_INDEX_BATCH_LIMIT = 1000;

    public IndexService(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    public IndexService(ElasticsearchConfig esConfig) {
        this.esClient = new ElasticsearchClient(new RestClientTransport(esConfig.restClient(), new JacksonJsonpMapper()));
    }

    public <T> void bulkIndex(String index, Stream<T> data) {
        Iterators.partition(data.iterator(), BULK_INDEX_BATCH_LIMIT).forEachRemaining(list -> bulkIndex(index, list));
    }

    public <T> int bulkIndex(String index, List<T> data) {
        log.info("Indexing {} documents in bulk", data.size());
        BulkRequest.Builder requestBuilder = new BulkRequest.Builder();
        for (T record : data) {
            requestBuilder.operations(op -> op.index(idx -> idx.index(index).document(record)));
        }
        BulkResponse response;
        try {
            response = esClient.bulk(requestBuilder.build());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        log.info("Processed {} documents. Took {} ms", response.items().size(), response.took());
        logErrors(response);
        return response.items().size();
    }

    private static void logErrors(BulkResponse response) {
        if (response.errors()) {
            log.warn("Bulk indexing returned error(s)");
            response.items().stream()
                .filter(i -> i.error() != null)
                .forEach(i -> log.warn(i.error().reason()));
            System.exit(1);
        }
    }
    
}
