package com.einott.search.command;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.einott.search.domain.RedditRecord;
import com.einott.search.service.RedditSearchService;

import java.util.List;

public class MatchBodyBoostByScoreCommand implements SearchCommand {
    @Override
    public List<Hit<RedditRecord>> execute(RedditSearchService searchService, String[] args) {
        SearchCommand.assertArgsSize(args, 2);
        Integer boostScore;
        try {
            boostScore = Integer.valueOf(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
        return searchService.matchBodyBoostByScore(args[0], boostScore);
    }
}
