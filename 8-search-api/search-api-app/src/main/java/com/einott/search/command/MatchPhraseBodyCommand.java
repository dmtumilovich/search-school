package com.einott.search.command;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.einott.search.domain.RedditRecord;
import com.einott.search.service.RedditSearchService;

import java.util.List;

public class MatchPhraseBodyCommand implements SearchCommand {
    @Override
    public List<Hit<RedditRecord>> execute(RedditSearchService searchService, String[] args) {
        SearchCommand.assertArgsSize(args, 1);
        return searchService.matchPhraseBody(args[0]);
    }
}
