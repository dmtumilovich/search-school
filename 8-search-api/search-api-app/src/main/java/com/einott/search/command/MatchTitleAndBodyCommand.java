package com.einott.search.command;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.einott.search.domain.RedditRecord;
import com.einott.search.service.RedditSearchService;

import java.util.List;

public class MatchTitleAndBodyCommand implements SearchCommand {


    @Override
    public List<Hit<RedditRecord>> execute(RedditSearchService searchService, String[] args) {
        SearchCommand.assertArgsSize(args, 2);
        return searchService.matchTitleAndBody(args[0], args[1]);
    }
}
