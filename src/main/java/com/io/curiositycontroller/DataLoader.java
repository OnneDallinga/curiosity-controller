package com.io.curiositycontroller;

import com.io.curiositycontroller.tedtalk.TedTalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Enable this to populate database
@Configuration
public class DataLoader implements ApplicationRunner {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    public DataLoader(ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread.sleep(10000); // TODO: Fix docker-compose to depend on ES with health check so this isn't needed any more
        List<TedTalk> tedTalks = parseCsv("data.csv");
        elasticsearchRestTemplate.save(tedTalks);
    }

    protected List<TedTalk> parseCsv(String fileLocation) {
        List<TedTalk> tedTalks = new ArrayList<>();

        try {
            Files.lines(Path.of(fileLocation)).skip(1).forEach(line -> {
                String[] split = line.split(",");
                // Titles can contain commas, which is also our delimiter so that's problematic
                // Fortunately this is only the case with the title
                if (split.length == 6) {
                    tedTalks.add(TedTalk.builder()
                            .id(UUID.randomUUID().toString())
                            .title(split[0])
                            .author(split[1])
                            .date(split[2])
                            .views(parseLongOrReturnZero(split[3]))
                            .likes(parseLongOrReturnZero(split[4]))
                            .link(split[5])
                            .build());
                } else {
                    int index = 0;
                    String title = "";

                    while (index <= split.length - 6) {
                        title = title.concat(split[index]);
                        index++;
                    }

                    tedTalks.add(TedTalk.builder()
                            .id(UUID.randomUUID().toString())
                            .title(title)
                            .author(split[index++])
                            .date(split[index++])
                            .views(parseLongOrReturnZero(split[index++]))
                            .likes(parseLongOrReturnZero(split[index++]))
                            .link(split[index])
                            .build());
                }

            });
        } catch (IOException e) {
            System.out.println(e);
        }
        return tedTalks;
    }

    protected long parseLongOrReturnZero(String potentialLong) {
        long result;
        try {
            result = Long.parseLong(potentialLong);
        } catch (NumberFormatException exception) {
            result = 0;
        }
        return result;
    }


}
