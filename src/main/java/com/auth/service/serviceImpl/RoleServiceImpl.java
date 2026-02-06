package com.auth.service.serviceImpl;

import com.auth.service.exception.NotFoundException;
import com.auth.service.repository.RoleRepository;
import com.auth.service.model.Role;
import com.auth.service.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {


    private final RoleRepository roleRepository;

    @Override
    public Role createRole(Role roles) {
        return  roleRepository.save(roles);

    }

    @Override
    public List<Role> getRoles(Integer page, Integer count, Integer status) {
        Pageable pageable = PageRequest.of(page, count);
        Page<Role> pageResponse =   roleRepository.findAll(pageable);
        return pageResponse.getContent();
    }

    @Override
    public Role getRoleById(Integer roleId) {
        Role roles =   roleRepository.findById(roleId).orElseThrow(() -> new NotFoundException("Role not found with ID: " + roleId));
        return roles;
    }
}
