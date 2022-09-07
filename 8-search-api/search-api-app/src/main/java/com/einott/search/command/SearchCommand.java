package com.einott.search.command;

import co.elastic.clients.elasticsearch.core.search.Hit;
import com.einott.search.domain.RedditRecord;
import com.einott.search.service.RedditSearchService;

import java.util.List;
import java.util.Map;

import static com.einott.search.AppConstants.*;

public interface SearchCommand {

    Map<String, ? extends SearchCommand> commands = Map.of(
        MATCH_BODY_COMMAND, new MatchBodyCommand(),
        MATCH_PHRASE_BODY_COMMAND, new MatchPhraseBodyCommand(),
        MATCH_TITLE_AND_BODY_COMMAND, new MatchTitleAndBodyCommand(),
        MATCH_BODY_BOOST_BY_SCORE_COMMAND, new MatchBodyBoostByScoreCommand(),
        MATCH_BODY_BOOST_BY_DATE_COMMAND, new MatchBodyBoostByDateCommand(),
        POSTS_BETWEEN_DATES_COMMAND, new PostsBetweenDatesCommand(),
        POSTS_FOR_USER_ID, new PostsForUserIdCommand()
    );

    List<Hit<RedditRecord>> execute(RedditSearchService searchService, String[] args);

    static SearchCommand get(String commandName) {
        if (commands.containsKey(commandName)) {
            return commands.get(commandName);
        } else {
            throw new IllegalArgumentException("Invalid command name: " + commandName);
        }
    }

    static void assertArgsSize(String[] args, int expected) {
        if (args.length != expected) {
            throw new IllegalArgumentException(String.format("Invalid number of input args (expected: %s, actual: %s)", expected, args.length));
        }
    }

}
