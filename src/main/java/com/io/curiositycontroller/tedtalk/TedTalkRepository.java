package com.io.curiositycontroller.tedtalk;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

@Repository
public class TedTalkRepository {

    private final ElasticsearchRestTemplate elasticSearchRestTemplate;
    private final Map<String, String> parameterFieldMap = Map.of(
            TITLE, "title.keyword",
            AUTHOR, "author.keyword",
            DATE, "date.keyword",
            VIEWS, "views.keyword",
            LIKES, "likes.keyword",
            LINK, "link.keyword"
    );
    private final Map<String, Float> fields = Map.of(
            TITLE, 1f,
            AUTHOR, 1f
    );
    private static final String TITLE = "title";
    private static final String AUTHOR = "author";
    private static final String DATE = "date";
    private static final String VIEWS = "views";
    private static final String LIKES = "likes";
    private static final String LINK = "link";

    public TedTalkRepository(ElasticsearchRestTemplate elasticSearchRestTemplate) {
        this.elasticSearchRestTemplate = elasticSearchRestTemplate;
    }

    public SearchHits<TedTalk> search(MultiValueMap<String, String> params) {
        Query query = buildSearchQuery(params);
        return elasticSearchRestTemplate.search(query, TedTalk.class);
    }

    public void delete(String id) {
        elasticSearchRestTemplate.delete(id, TedTalk.class);
    }

    public void save(TedTalk tedTalk) {
        elasticSearchRestTemplate.save(tedTalk);
    }

    public TedTalk getById(String id) {
        return elasticSearchRestTemplate.get(id, TedTalk.class);
    }

    public NativeSearchQuery buildSearchQuery(MultiValueMap<String, String> params) {
        BoolQueryBuilder query = buildQuery(params);
        BoolQueryBuilder filter = buildFilter(params);

        return new NativeSearchQueryBuilder()
                .withQuery(query)
                .withFilter(filter)
                .build();
    }

    public BoolQueryBuilder buildQuery(MultiValueMap<String, String> params) {
        BoolQueryBuilder query = new BoolQueryBuilder();
        String terms = params.getFirst("terms");
        if (StringUtils.isNotBlank(terms)) {
            query.must(QueryBuilders
                    .queryStringQuery("*" + terms + "*")
                    .fields(fields));
        }

        return query;
    }

    public BoolQueryBuilder buildFilter(MultiValueMap<String, String> params) {
        BoolQueryBuilder query = new BoolQueryBuilder();

        addAggregation(params, TITLE, query);
        addAggregation(params, AUTHOR, query);
        addAggregation(params, DATE, query);
        addAggregation(params, LIKES, query);
        addAggregation(params, VIEWS, query);
        addAggregation(params, LINK, query);

        return query;
    }

    private void addAggregation(MultiValueMap<String, String> params, String name, BoolQueryBuilder query) {
        List<String> values = params.get(name);
        if (!CollectionUtils.isEmpty(values)) {
            query.must(QueryBuilders.termsQuery(parameterFieldMap.get(name), values));
        }
    }

}
