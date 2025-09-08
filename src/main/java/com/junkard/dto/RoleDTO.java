package com.junkard.dto;

import java.util.Set;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO {
    private Long id;

    @NotBlank
    private String name;

    private Set<Long> permissionIds;
}