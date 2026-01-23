package com.example.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

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

        // RESTHeart-managed fields — must not be sent
        document.remove("_id");
        document.remove("_etag");
        document.remove("_rev");

        return restHeartWebClient
                .post()
                .uri("workflow_platform/{collection}", collection)
                .bodyValue(document)
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(err -> {
                                    log.error("RESTHeart create failed [{}]: {}", response.statusCode(), err);
                                    return Mono.error(new RuntimeException("RESTHeart insert failed"));
                                })
                )
                .bodyToMono(Map.class)
                .map(createdDoc -> {
                    log.info("RESTHeart created document: {}", createdDoc);
                    return objectMapper.convertValue(createdDoc, entityClass);
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


    public Flux<Map> getWithFilter(String collection, Map<String, Object> equalsFilters) {
        try {
            Map<String, Object> mongoFilter =
                    equalsFilters.entrySet()
                            .stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> Map.of("$eq", e.getValue())
                            ));

            String filterJson = objectMapper.writeValueAsString(mongoFilter);

            return restHeartWebClient
                    .get()
                    .uri("workflow_platform/{collection}?filter={filter}",
                            Map.of(
                                    "collection", collection,
                                    "filter", filterJson
                            ))
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
                .uri(uriBuilder ->
                        uriBuilder
                                .path("workflow_platform/{collection}/{id}")
                                .build(collection, documentId)
                )
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        // 204 on success
                        return Mono.empty();
                    }
                    if (response.statusCode().value() == 404) {
                        return Mono.error(new RuntimeException(
                                "Document not found with id: " + documentId
                        ));
                    }if (response.statusCode().value() == 409) {
                        return Mono.error(new RuntimeException(
                                "Conflict deleting document (possible revision mismatch)"
                        ));
                    }
                    return response.bodyToMono(String.class)
                            .flatMap(err -> Mono.error(new RuntimeException(
                                    "RESTHeart delete failed: " + err
                            )));
                });
    }


    public <T> Mono<T> upsert(
            String collection,
            String id,
            T object,
            Class<T> entityClass
    ) {
        Map<String, Object> document = objectMapper.convertValue(object, Map.class);

        // RESTHeart-managed fields — never send
        document.remove("_id");
        document.remove("_etag");
        document.remove("_rev");

        return restHeartWebClient
                .put()
                .uri("workflow_platform/{collection}/{id}", collection, id)
                .bodyValue(document)
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(err -> Mono.error(
                                        new RuntimeException("RESTHeart upsert failed: " + err)
                                ))
                )
                .bodyToMono(Map.class)
                .map(updatedDoc -> {
                    log.info("RESTHeart upserted document [{}]: {}", id, updatedDoc);
                    return objectMapper.convertValue(updatedDoc, entityClass);
                });
    }

}
