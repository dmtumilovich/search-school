package com.einott.search.command;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.einott.search.domain.RedditRecord;
import com.einott.search.service.RedditSearchService;

import java.time.LocalDateTime;
import java.util.List;

public class PostsBetweenDatesCommand implements SearchCommand {
    @Override
    public List<Hit<RedditRecord>> execute(RedditSearchService searchService, String[] args) {
        SearchCommand.assertArgsSize(args, 2);
        LocalDateTime startDt = LocalDateTime.parse(args[0]);
        LocalDateTime endDt = LocalDateTime.parse(args[1]);
        return searchService.postsBetweenDates(startDt, endDt);
    }
}
