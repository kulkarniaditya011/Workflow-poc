package com.example.backend.config;

import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Configuration
public class WebClientConfig {
    @Value("${restheart.base-url}")
    private String restHeartBaseUrl;

    @Value("${restheart.username:admin}")
    private String restHeartUsername;

    @Value("${restheart.password:secret}")
    private String restHeartPassword;

    @Bean
    public WebClient restHeartWebClient() {
        String basicAuth =Base64.getEncoder()
                .encodeToString((restHeartUsername + ":" + restHeartPassword).getBytes());

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer-> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();

        return WebClient.builder()
                .baseUrl(restHeartBaseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
