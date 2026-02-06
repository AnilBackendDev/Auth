package com.auth.service.service;

import com.auth.service.model.Role;

import java.util.List;

public interface RoleService {

    Role createRole(Role roles);

    List<Role> getRoles(Integer page, Integer count, Integer status);

    Role getRoleById(Integer roleid);
}
