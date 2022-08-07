package com.einott.search;

import java.util.stream.Stream;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import com.einott.search.domain.Flight;
import com.einott.search.util.CsvReader;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.BulkRequest.Builder;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main( String[] args ) throws Exception {
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        Stream<Flight> csvStream = CsvReader.readCsvLazy("flights.csv", Flight.class);
        BulkRequest.Builder requestBuilder = new BulkRequest.Builder();
        csvStream.forEach(f -> {
            requestBuilder.operations(op -> op.index(idx -> idx.index("flights")
                                                                .document(f)));
        });

        BulkResponse response = client.bulk(requestBuilder.build());
        if (response.errors()) {
            log.error("Bulk indexing returned error(s)");
            response.items().stream()
                .filter(i -> i.error() != null)
                .forEach(i -> log.error(i.error().reason()));
            System.exit(1);
        }
        
    }
}
