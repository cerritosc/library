package com.focusservices.library.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Set;

import java.time.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import com.focusservices.library.commons.Constants;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


import lombok.*;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "CC_ROL")
//@TablaVersionada(nombre = "ccRol", etiqueta = "Rol")
public class CcRol implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "SK_ROL")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ROL")
    @SequenceGenerator(name = "SEQ_ROL", sequenceName = "SEQ_ROL", allocationSize = 1)
    private Integer skRol; 

    @Column(name = "ST_DESCRIPCION" )
    @NotBlank(message = "No puede estar vacio el campo stDescripcion")
    @Size(max = 200, message = "El campo stDescripcion excede la longitud permitida")
    private String stDescripcion; 

    @Column(name = "FC_CREA_FECHA" )
    @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    @CreatedDate
    private LocalDateTime fcCreaFecha; 

    @Column(name = "FC_MOD_FECHA" )
    @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    @LastModifiedDate
    private LocalDateTime fcModFecha; 

    @Column(name = "ST_CREA_USUARIO" )
    @Size(max = 32, message = "El campo stCreaUsuario excede la longitud permitida")
    @CreatedBy
    private String stCreaUsuario; 

    @Column(name = "ST_MOD_USUARIO" )
    @Size(max = 32, message = "El campo stModUsuario excede la longitud permitida")
    @LastModifiedBy
    private String stModUsuario; 

    @Column(name = "BN_ESTADO" )
    @NotNull(message = "No puede estar vacio el campo bnEstado")
    private Boolean bnEstado; 

    @Column(name = "CD_ROL" )
    @Size(max = 50, message = "El campo cdRol excede la longitud permitida")
    private String cdRol; 



        @OneToMany(mappedBy = "ccRol", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<CcEmpleado> ccEmpleadoes;

        @OneToMany(mappedBy = "ccRol", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<SecRolePrivilege> secRolePrivilegees;

    public String getEstadoVisible() {
        return Boolean.TRUE.equals(bnEstado) ? "Activo" : "Inactivo";
    }


    // delegates de ids
}
