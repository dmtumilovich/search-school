package com.einott.search;

import java.util.stream.Stream;

import com.einott.search.conf.ElasticsearchConfig;
import com.einott.search.service.IndexService;
import com.einott.search.util.CsvReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

    public static void main( String[] args ) throws Exception {
        IndexService indexService = null;
        try {
            ElasticsearchConfig esConfig = ElasticsearchConfig.builder()
                .hostname(args[0])
                .port(Integer.parseInt(args[1]))
                .scheme(args[2])
                .username(args[3])
                .password(args[4])
                .build();
            indexService = new IndexService(esConfig);
            Class<?> pojoClass = Class.forName(args[7]);
            Stream<?> csvStream = CsvReader.readCsvLazy(args[5], pojoClass);
            indexService.bulkIndex(args[6], csvStream);
        } catch (Throwable e) {
            log.error("Error while processing", e);
        } finally {
            indexService.close();
        }
        
    }
}
