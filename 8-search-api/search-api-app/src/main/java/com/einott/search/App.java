package com.einott.search;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.einott.search.command.SearchCommand;
import com.einott.search.conf.ElasticsearchConfig;
import com.einott.search.domain.RedditRecord;
import com.einott.search.service.RedditSearchService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.einott.search.AppConstants.ES_CONFIG_PROPERTIES;
import static com.einott.search.AppConstants.REDDIT_INDEX;

@Slf4j
public final class App {
    private App() {}

    public static void main(String[] args) throws IOException {

        String command = args[0];
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        ElasticsearchConfig esConfig = ElasticsearchConfig.fromPropertiesFile(ES_CONFIG_PROPERTIES);
        RedditSearchService searchService = new RedditSearchService(esConfig, REDDIT_INDEX);

        SearchCommand searchCommand = SearchCommand.get(command);
        List<Hit<RedditRecord>> result = searchCommand.execute(searchService, commandArgs);

        result.forEach(h -> log.info("score: {}, id: {}, source: {}", h.score(), h.id(), h.source()));

        searchService.close();

    }
}
