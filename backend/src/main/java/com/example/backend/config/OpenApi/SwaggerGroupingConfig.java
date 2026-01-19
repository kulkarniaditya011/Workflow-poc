package com.example.backend.config.OpenApi;

import com.example.backend.annotations.AdminApi;
import com.example.backend.annotations.SharedApi;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerGroupingConfig {

    @Bean
    public GroupedOpenApi adminApis() {
        return GroupedOpenApi.builder()
                .group("admin")
                .addOperationCustomizer((operation, handlerMethod) -> {

                    boolean isAdmin = handlerMethod.hasMethodAnnotation(AdminApi.class);
                    boolean isShared = handlerMethod.hasMethodAnnotation(SharedApi.class);

                    return (isAdmin || isShared) ? operation : null;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi workflowApis() {
        return GroupedOpenApi.builder()
                .group("workflow")
                .addOperationCustomizer((operation, handlerMethod) -> {

                    boolean isAdmin = handlerMethod.hasMethodAnnotation(AdminApi.class);
                    boolean isShared = handlerMethod.hasMethodAnnotation(SharedApi.class);

                    if (!isAdmin || isShared) {
                        return operation;
                    }
                    return null;
                })
                .build();
    }


}
