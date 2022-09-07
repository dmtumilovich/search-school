package com.einott.search.util;

import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.einott.search.domain.RedditRecord;

public class CsvReaderTest {
    
    @Test
    void testReadRedditCsvLazy() {
        Stream<RedditRecord> redditStream = CsvReader.readCsvLazy(new InputStreamReader(getClass().getResourceAsStream("/subreddit.csv")), RedditRecord.class);
        Assertions.assertNotNull(redditStream);
        List<RedditRecord> redditList = redditStream.collect(Collectors.toList());
        Assertions.assertFalse(redditList.isEmpty());
        Assertions.assertEquals(4, redditList.size());
        RedditRecord firstRow = redditList.get(0);
        Assertions.assertEquals("Comment", firstRow.getTitle());
        Assertions.assertEquals(2, firstRow.getScore());
        Assertions.assertEquals("ejacj98", firstRow.getId());
        Assertions.assertEquals("", firstRow.getUrl());
        Assertions.assertEquals(1, firstRow.getCommsNum());
        Assertions.assertEquals(1553485820L, firstRow.getCreated());
        Assertions.assertEquals("The \"myth\" you're debunking is in regards to the childhood schedule. ZERO OF THOSE VACCINES CONTAIN IT.  You're being a pedantic fuck for no reason. ", firstRow.getBody());
        Assertions.assertEquals(LocalDateTime.parse("2019-03-25T05:50:20"), firstRow.getTimestamp());
    }

}
