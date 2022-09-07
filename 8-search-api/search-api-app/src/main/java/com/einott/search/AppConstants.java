package com.einott.search;

public class AppConstants {

    public static String REDDIT_INDEX = "reddit_vaccine";

    public static String MATCH_BODY_COMMAND = "match_body";
    public static String MATCH_PHRASE_BODY_COMMAND = "match_phrase_body";
    public static String MATCH_TITLE_AND_BODY_COMMAND = "match_title_and_body";
    public static String MATCH_BODY_BOOST_BY_SCORE_COMMAND = "match_body_boost_score";
    public static String MATCH_BODY_BOOST_BY_DATE_COMMAND = "match_body_boost_date";
    public static String POSTS_BETWEEN_DATES_COMMAND = "posts_between_dates";
    public static String POSTS_FOR_USER_ID = "posts_for_user_id";

    public static String ES_CONFIG_PROPERTIES = "es.config";

    private AppConstants() {}

}
