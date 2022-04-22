package com.io.curiositycontroller.component;

import com.io.curiositycontroller.CuriosityControllerApplication;
import com.io.curiositycontroller.tedtalk.TedTalk;
import com.io.curiositycontroller.tedtalk.TedTalkDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = CuriosityControllerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class ComponentTest {

    private final String HOST_URL = "http://localhost:8080/";

    private static GenericContainer<?> elasticSearchContainer;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @BeforeAll
    static void configureContainer() {
        elasticSearchContainer = new FixedHostPortGenericContainer("docker.elastic.co/elasticsearch/elasticsearch:7.6.2")
                .withFixedExposedPort(9200, 9200)
                .withEnv("discovery.type", "single-node")
                .waitingFor(Wait.forLogMessage(".*started.*", 1));
        elasticSearchContainer.start();
    }

    @AfterEach
    void shutDown() {
        elasticsearchRestTemplate.indexOps(TedTalk.class).delete();
    }

    @AfterAll
    static void destroy() {
        elasticSearchContainer.stop();
    }

    @Test
    void createTedTalk() {
        elasticsearchRestTemplate.indexOps(TedTalk.class).create();
        TedTalkDto tedTalkDto = TedTalkDto.builder()
                .author("author")
                .title("title")
                .date("date")
                .link("https://www.com")
                .build();

        String id = given()
                .body(tedTalkDto)
                .when().contentType(ContentType.JSON).post(HOST_URL + "ted-talks")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        assertThat(id, is(notNullValue()));
        TedTalk tedTalk = elasticsearchRestTemplate.get(id, TedTalk.class);
        assertThat(tedTalk, is(notNullValue()));
        assertEquals(tedTalk.getTitle(), tedTalkDto.getTitle());
        assertEquals(tedTalk.getAuthor(), tedTalkDto.getAuthor());
        assertEquals(tedTalk.getDate(), tedTalkDto.getDate());
        assertEquals(tedTalk.getLink(), tedTalkDto.getLink());
    }

}
