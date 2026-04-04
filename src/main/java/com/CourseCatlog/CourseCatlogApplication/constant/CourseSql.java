package com.CourseCatlog.CourseCatlogApplication.constant;

public final class CourseSql {

    private CourseSql() {}

    public static final String SEARCH_FULLTEXT = """
            SELECT c.* FROM courses c
            WHERE to_tsvector('english', coalesce(c.title, '') || ' ' || coalesce(c.description, ''))
                  @@ plainto_tsquery('english', :q)
            """;

    public static final String SEARCH_FULLTEXT_COUNT = """
            SELECT count(*) FROM courses c
            WHERE to_tsvector('english', coalesce(c.title, '') || ' ' || coalesce(c.description, ''))
                  @@ plainto_tsquery('english', :q)
            """;
}
