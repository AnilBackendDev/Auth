package com.auth.service.controller;


import com.auth.service.dto.ApiResponse;
import com.auth.service.exception.NotFoundException;
import com.auth.service.model.Role;
import com.auth.service.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//import static com.ooge.catalogmangement.service.impl.InventoryServiceImpl.log;

@Slf4j
@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {


    private final RoleService roleService;


    @PostMapping("/createRole")
    public ResponseEntity<ApiResponse> createRole(@RequestBody @Valid Role roles) {
        log.info("Creating new role: {}", roles);
        ApiResponse response = new ApiResponse("Role created successfully", roleService.createRole(roles));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/getroles")
    public ResponseEntity<ApiResponse> getRoles(@RequestParam(defaultValue = "0") Integer page,
                                             @RequestParam(defaultValue = "10") Integer count,
                                             @RequestParam(defaultValue = "1") Integer status) {
        log.info("Fetching roles with page: {}, count: {}, status: {}", page, count, status);
        List<Role> roles = roleService.getRoles(page, count, status);
        return ResponseEntity.ok(new ApiResponse("Roles fetched successfully", roles));
    }

    @GetMapping("/getrolebyid/{roleid}")
    public ResponseEntity<ApiResponse> getRoleById(@PathVariable Integer roleid) {
        try {
            log.info("Fetching role with ID: {}", roleid);
            return ResponseEntity.ok(new ApiResponse("Role fetched successfully", roleService.getRoleById(roleid)));
        } catch (NotFoundException e) {
            log.warn("Role ID {} not found: {}", roleid, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        }
    }


}
