package com.io.curiositycontroller.configuration;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Profile({"test"})
public class ElasticSearchConfigurationTest extends ElasticSearchConfiguration {

    @Bean
    @Override
    public RestHighLevelClient client() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .withConnectTimeout(10000)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}
