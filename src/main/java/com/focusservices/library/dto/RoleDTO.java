package com.focusservices.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author VOlivares
 */
@Data
@EqualsAndHashCode
@AllArgsConstructor
public class RoleDTO {
    private String nombreRol;
    private String descripcionRol;
}
