package com.junkard.dto;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoleUpdateDTO {
    @NotNull
    private Long roleId;
}