package com.einott.search;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;

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
        SSLContext sslContext = SSLContexts.custom()
            .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
            .build();

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "StrongAdminPassword"));
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "https"))
            .setHttpClientConfigCallback(new HttpClientConfigCallback() {

                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    return httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)
                        // .setSSLHostnameVerifier((s, sslSession) -> true); // hostname in the certificate is not localhost -- disable SSL hostname verification
                        .setSSLContext(sslContext)
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                }
                
            })
            .build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);

        Stream<Flight> csvStream = CsvReader.readCsvLazy("/Users/Dzmitry_Tumilovich/work/search-school/6-indexing/flights.csv", Flight.class);
        BulkRequest.Builder requestBuilder = new BulkRequest.Builder();
        csvStream
            .limit(1000) // TODO
            .forEach(f -> {
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
