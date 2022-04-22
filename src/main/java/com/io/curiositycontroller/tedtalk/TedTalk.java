package com.io.curiositycontroller.tedtalk;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Builder(toBuilder = true)
@Document(indexName = "ted-talk")
public class TedTalk {

    @Id
    private String id;
    private String title;
    private String author;
    private String date; // TODO: Refactor to a usable Instant
    private long views;
    private long likes;
    private String link;

}
