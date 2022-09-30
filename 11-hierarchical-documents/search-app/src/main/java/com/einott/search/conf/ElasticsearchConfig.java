package com.einott.search.conf;

import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
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

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Builder
public class ElasticsearchConfig {

    private String hostname;
    private int port;
    private String scheme;
    private String username;
    private String password;

    public RestClient restClient() {
        log.info("Initializing rest client for {}://{}:{}", this.scheme, this.hostname, this.port);
        try {
            SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                .build();
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(this.username, this.password));
            RestClient restClient = RestClient.builder(new HttpHost(this.hostname, this.port, this.scheme))
                .setHttpClientConfigCallback(new HttpClientConfigCallback() {

                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider)
                            .setSSLContext(sslContext)
                            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE); // disable SSL verification
                    }

                })
                .build();
            return restClient;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create rest client!", e);
        }
    }

    public JacksonJsonpMapper jacksonJsonpMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new JacksonJsonpMapper(objectMapper);
    }

    public static ElasticsearchConfig fromPropertiesFile(String propertiesFile) {
        try (InputStream inputStream = ElasticsearchConfig.class.getClassLoader().getResourceAsStream(propertiesFile)) {

            if (inputStream == null) {
                throw new IllegalArgumentException("Unable to locate properties file: " + propertiesFile);
            }
            Properties props = new Properties();
            props.load(inputStream);
            return ElasticsearchConfig.builder()
                .hostname(props.getProperty("hostname"))
                .port(Integer.parseInt(props.getProperty("port")))
                .scheme(props.getProperty("scheme"))
                .username(props.getProperty("username"))
                .password(props.getProperty("password"))
                .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
