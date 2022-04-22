package com.io.curiositycontroller.tedtalk;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.util.LinkedMultiValueMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = TedTalkRepository.class)
class TedTalkRepositoryTest {

    @Autowired
    private TedTalkRepository tedTalkRepository;

    @MockBean
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    void buildQuery_shouldBuildQuery() {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("terms", "test");

        BoolQueryBuilder boolQueryBuilder = tedTalkRepository.buildQuery(params);
        assertThat(boolQueryBuilder.toString(), is("""
                {
                  "bool" : {
                    "must" : [
                      {
                        "query_string" : {
                          "query" : "*test*",
                          "fields" : [
                            "author^1.0",
                            "title^1.0"
                          ],
                          "type" : "best_fields",
                          "default_operator" : "or",
                          "max_determinized_states" : 10000,
                          "enable_position_increments" : true,
                          "fuzziness" : "AUTO",
                          "fuzzy_prefix_length" : 0,
                          "fuzzy_max_expansions" : 50,
                          "phrase_slop" : 0,
                          "escape" : false,
                          "auto_generate_synonyms_phrase_query" : true,
                          "fuzzy_transpositions" : true,
                          "boost" : 1.0
                        }
                      }
                    ],
                    "adjust_pure_negative" : true,
                    "boost" : 1.0
                  }
                }"""));
    }

    @Test
    void buildFilter_shouldBuildFilter() {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("title", "title");
        params.set("author", "author");
        params.set("likes", "likes");
        params.set("views", "views");

        BoolQueryBuilder boolQueryBuilder = tedTalkRepository.buildFilter(params);
        assertThat(boolQueryBuilder.toString(), is("""
                {
                  "bool" : {
                    "must" : [
                      {
                        "terms" : {
                          "title.keyword" : [
                            "title"
                          ],
                          "boost" : 1.0
                        }
                      },
                      {
                        "terms" : {
                          "author.keyword" : [
                            "author"
                          ],
                          "boost" : 1.0
                        }
                      },
                      {
                        "terms" : {
                          "likes.keyword" : [
                            "likes"
                          ],
                          "boost" : 1.0
                        }
                      },
                      {
                        "terms" : {
                          "views.keyword" : [
                            "views"
                          ],
                          "boost" : 1.0
                        }
                      }
                    ],
                    "adjust_pure_negative" : true,
                    "boost" : 1.0
                  }
                }"""));
    }

    @Test
    void search_shouldSearch() {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        tedTalkRepository.search(params);
        verify(elasticsearchRestTemplate, times(1)).search(any(Query.class), any());
    }

    @Test
    void getById_shouldGetById() {
        String id = "some-uuid";
        tedTalkRepository.getById(id);
        verify(elasticsearchRestTemplate, times(1)).get(eq(id), eq(TedTalk.class));
    }

    @Test
    void delete_shouldDelete() {
        String id = "some-uuid";
        tedTalkRepository.delete(id);
        verify(elasticsearchRestTemplate, times(1)).delete(eq(id), eq(TedTalk.class));
    }

}
