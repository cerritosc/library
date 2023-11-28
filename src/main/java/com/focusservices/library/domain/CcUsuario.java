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
@Table(name = "CC_USUARIO")
//@TablaVersionada(nombre = "ccUsuario", etiqueta = "Usuario")
public class CcUsuario implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "SK_USUARIO")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USUARIO")
    @SequenceGenerator(name = "SEQ_USUARIO", sequenceName = "SEQ_USUARIO", allocationSize = 1)
    private Integer skUsuario; 

    @Column(name = "CD_USUARIO" )
    @NotBlank(message = "No puede estar vacio el campo cdUsuario")
    @Size(max = 32, message = "El campo cdUsuario excede la longitud permitida")
    private String cdUsuario; 

    @Column(name = "FC_CREA_FECHA" )
    @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    @CreatedDate
    private LocalDateTime fcCreaFecha; 

    @Column(name = "FC_FIN" )
    @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime fcFin; 

    @Column(name = "FC_INICIO" )
    @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    private LocalDateTime fcInicio; 

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

    @Column(name = "ST_OBSERVACION" )
    @Size(max = 500, message = "El campo stObservacion excede la longitud permitida")
    private String stObservacion; 

        @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "FK_EMPLEADO", referencedColumnName = "SK_EMPLEADO")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private CcEmpleado ccEmpleado; 




    // delegates de ids
    public Integer getCcEmpleadoSkEmpleadoDelegate() {
        if(ccEmpleado != null) {
            return ccEmpleado.getSkEmpleado();
        }
        else return null;
    } 
    
    public String getCcEmpleadoStDescripcionDelegate() {
        if(ccEmpleado != null) {
            return ccEmpleado.getStNombres() + " " + ccEmpleado.getStApellidos();
        }
        else return "";
    }

    public String getCcEmpleadoSelect2Delegate() {
        return String.valueOf(getCcEmpleadoSkEmpleadoDelegate())
            + "|"
            + getCcEmpleadoStDescripcionDelegate();
    }
}
