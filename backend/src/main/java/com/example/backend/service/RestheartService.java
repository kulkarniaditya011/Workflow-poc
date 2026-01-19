package com.example.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
public class RestheartService {
    private final WebClient restHeartWebClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public RestheartService(WebClient restHeartWebClient, ObjectMapper objectMapper) {
        this.restHeartWebClient = restHeartWebClient;

        this.objectMapper = objectMapper;
    }

    public <T> Mono<T> create(String collection, T object, Class<T> entityClass) {
        Map<String, Object> document = objectMapper.convertValue(object, Map.class);
//        log.info("Getting the form object from form Service:{}", document.toString());
        document.remove("_etag");
        document.remove("_rev");

        return restHeartWebClient
                .post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("workflow_platform/{collection}")
                                .build(collection)
                )
                .bodyValue(document)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(Map.class)
                                .doOnNext(body -> log.info("RESTHeart created: {}", body))
                                .thenReturn(object);
                    } else {
                        return response.bodyToMono(String.class)
                                .flatMap(err -> {
                                    log.error("RESTHeart error {} → {}", response.statusCode(), err);
                                    return Mono.error(new RuntimeException("RESTHeart insert failed"));
                                });
                    }
                });

    }

    public Mono<Map> findById(String collection, String id){
        return restHeartWebClient
                .get()
                .uri("workflow_platform/{collection}/{id}", collection, id)
                .retrieve()
                .bodyToMono(Map.class);
    }

    public Flux<Map> getAll(String collection){
      return restHeartWebClient
                .get()
                .uri("workflow_platform/{collection}", collection)
                .retrieve()
                .bodyToFlux(Map.class);
    }

    public Flux<Map> getWithQuery(String collection, Map<String, Object> queryParams) {
        return restHeartWebClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path("{collection}");
                    queryParams.forEach(uriBuilder::queryParam);
                    return uriBuilder.build(collection);
                })
                .retrieve()
                .bodyToFlux(Map.class);
    }

    // In RestHeartService
    public Flux<Map> getWithFilter(String collection, Map<String, Object> filterCriteria) {
        try {
            String filterJson = objectMapper.writeValueAsString(filterCriteria);
//            log.info("Filtering form object from form Service: {}", filterJson);

            return restHeartWebClient
                    .get()
                    .uri("workflow_platform/{collection}?filter={filter}",
                            Map.of("collection", collection, "filter", filterJson))
                    .retrieve()
                    .bodyToFlux(Map.class);

        } catch (JsonProcessingException e) {
            return Flux.error(new RuntimeException("Failed to serialize filter", e));
        }
    }



    public Mono<Map> patch(String collection, String documentId, Map<String, Object> updates) {
        return restHeartWebClient
                .patch()
                .uri("workflow_platform/{collection}/{id}", collection, documentId)
                .bodyValue(updates)
                .retrieve()
                .bodyToMono(Map.class);
    }

    public Mono<Void> delete(String collection, String documentId) {
        return restHeartWebClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/workflow_platform/{collection}/{id}")
                        .build(collection, documentId))
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("Document not found"))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new RuntimeException("RESTHeart error"))
                )
                .bodyToMono(Void.class);
    }


    public <T> Mono<T> upsert(String collection, String id, T object) {
        Map<String, Object> document = objectMapper.convertValue(object, Map.class);

        // RESTHeart must control these
        document.remove("_id");
        document.remove("_etag");
        document.remove("_rev");

        return restHeartWebClient
                .put()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/workflow_platform/{collection}/{id}")
                                .build(collection, id)
                )
                .bodyValue(document)
                .retrieve()
                .toBodilessEntity()
                .thenReturn(object);
    }

}
