package com.junkard.dto;

import java.util.Set;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePermissionUpdateDTO {

    // A requisição deve conter a lista de IDs de permissão.
    @NotNull(message = "Permission IDs list cannot be null")
    private Set<Long> permissionIds;
}