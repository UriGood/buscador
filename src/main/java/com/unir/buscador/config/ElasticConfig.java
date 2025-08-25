package com.unir.buscador.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder(HttpHost.create("https://my-elasticsearch-project-fdbec4.es.us-central1.gcp.elastic.cloud:443"))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey dXhWUDRKZ0JxWDg5MU1YOWlCQ2w6X0dfNkNENzk5YWdCNE8xUkdid3NXZw==")
                })
                .build();
    }

    @Bean
    public ElasticsearchTransport transport(RestClient restClient) {
        return new RestClientTransport(restClient, new JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }
}