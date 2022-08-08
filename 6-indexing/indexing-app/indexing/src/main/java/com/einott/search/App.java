package com.einott.search;

import java.util.stream.Stream;

import com.einott.search.conf.ElasticsearchConfig;
import com.einott.search.domain.Flight;
import com.einott.search.service.IndexService;
import com.einott.search.util.CsvReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    public static void main( String[] args ) throws Exception {
        try {
            ElasticsearchConfig esConfig = ElasticsearchConfig.builder()
                .hostname(args[0])
                .port(Integer.parseInt(args[1]))
                .scheme(args[2])
                .username(args[3])
                .password(args[4])
                .build();
            IndexService indexService = new IndexService(esConfig);
            Stream<Flight> csvStream = CsvReader.readCsvLazy(args[5], Flight.class);
            indexService.bulkIndex(args[6], csvStream);
        } catch (Throwable e) {
            log.error("Error while processing", e);
        }
        
    }
}
