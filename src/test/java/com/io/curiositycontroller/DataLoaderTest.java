package com.io.curiositycontroller;

import com.io.curiositycontroller.tedtalk.TedTalk;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@SpringBootTest(classes = DataLoader.class)
class DataLoaderTest {

    @MockBean
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private DataLoader dataLoader;

    @Test
    void parseCsv_shouldReturnTedTalks() {
        List<TedTalk> tedTalks = dataLoader.parseCsv("src\\main\\resources\\data.csv");
        assertThat(tedTalks.size(), is(5443));
    }

    @Test
    void parseLongOrReturnZero_shouldParseLongOrReturnZero() {
        assertThat(dataLoader.parseLongOrReturnZero("55"), is(55L));
        assertThat(dataLoader.parseLongOrReturnZero("no"), is(0L));
    }

}
