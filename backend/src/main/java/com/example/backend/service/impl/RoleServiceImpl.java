package com.example.backend.service.impl;

import com.example.backend.common.PagebleObject;
import com.example.backend.common.ResponseUtil;
import com.example.backend.common.ValidationUtil;
import com.example.backend.dto.RoleDTO;
import com.example.backend.exceptions.RestApiException;
import com.example.backend.model.Roles;
import com.example.backend.response.ApiResponse;
import com.example.backend.service.RestheartService;
import com.example.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RestheartService restheartService;
    private final ValidationUtil validationUtil;
    private final PagebleObject pagebleObject;

    @Override
    public ApiResponse<String> createRole(RoleDTO roleDTO) {
        validationUtil.validate(roleDTO);
        Map<String, Object> filter = Map.of("name", roleDTO.getName());
        if (restheartService.getWithFilter("roles",  filter)
                .map(obj-> pagebleObject.convertValue(obj, Roles.class))
                .blockFirst()
                != null) {
            throw new RestApiException(
                    String.format("Role with name %s already exists", roleDTO.getName()),
                    HttpStatus.BAD_REQUEST);
        }
        Roles roles = Roles.builder()
                .name(roleDTO.getName())
                .authorities(roleDTO.getAuthorities())
                .build();
        RoleDTO roleDTOs = pagebleObject.map(restheartService.create("roles", roles, Roles.class).block(), RoleDTO.class);
        log.info("Role created: {}", roleDTOs.toString());
        return ResponseUtil.getResponseMessage("Role: " +roleDTOs.getName()+ " has been created");
    }

}
