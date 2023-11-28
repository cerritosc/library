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
import org.hibernate.annotations.Formula;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "CC_EMPLEADO")
//@TablaVersionada(nombre = "ccSupervisor", etiqueta = "Empleado")
public class CcEmpleado implements Serializable {

    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "SK_EMPLEADO")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EMPLEADO")
    @SequenceGenerator(name = "SEQ_EMPLEADO", sequenceName = "SEQ_EMPLEADO", allocationSize = 1)
    private Integer skEmpleado;

    @Column(name = "ST_NOMBRES")
    @NotBlank(message = "No puede estar vacio el campo stNombres")
    @Size(max = 200, message = "El campo stNombres excede la longitud permitida")
    private String stNombres;

    @Column(name = "ST_APELLIDOS")
    @NotBlank(message = "No puede estar vacio el campo stApellidos")
    @Size(max = 200, message = "El campo stApellidos excede la longitud permitida")
    private String stApellidos;

    @Column(name = "FC_CREA_FECHA")
    @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    @CreatedDate
    private LocalDateTime fcCreaFecha;

    @Column(name = "FC_MOD_FECHA")
    @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    @LastModifiedDate
    private LocalDateTime fcModFecha;

    @Column(name = "ST_CREA_USUARIO")
    @Size(max = 32, message = "El campo stCreaUsuario excede la longitud permitida")
    @CreatedBy
    private String stCreaUsuario;

    @Column(name = "ST_MOD_USUARIO")
    @Size(max = 32, message = "El campo stModUsuario excede la longitud permitida")
    @LastModifiedBy
    private String stModUsuario;

    @Column(name = "BN_ESTADO")
    @NotNull(message = "No puede estar vacio el campo bnEstado")
    private Boolean bnEstado;

    @Formula("CONCAT(CONCAT(ST_NOMBRES, ' '), ST_APELLIDOS)")
    private String stNombreCompleto;

    @OneToMany(mappedBy = "ccEmpleado", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<CcUsuario> ccUsuarioes;

    @Getter(onMethod = @__(@JsonIgnore))
    @JoinColumn(name = "FK_ROL", referencedColumnName = "SK_ROL")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private CcRol ccRol;

    @Getter(onMethod = @__(@JsonIgnore))
    @JoinColumn(name = "FK_SUPERVISOR", referencedColumnName = "SK_EMPLEADO")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private CcEmpleado ccSupervisor;

    public String getEstadoVisible() {
        return Boolean.TRUE.equals(bnEstado) ? "Activo" : "Inactivo";
    }

    // delegates de ids
    public Integer getCcRolSkRolDelegate() {
        if (ccRol != null) {
            return ccRol.getSkRol();
        } else {
            return null;
        }
    }

    public String getCcRolStDescripcionDelegate() {
        if (ccRol != null) {
            return ccRol.getStDescripcion();
        } else {
            return "";
        }
    }

    public String getCcRolSelect2Delegate() {
        return String.valueOf(getCcRolSkRolDelegate())
                + "|"
                + getCcRolStDescripcionDelegate();
    }

    public Integer getSupervisorIdDelegate() {
        if (ccSupervisor != null) {
            return ccSupervisor.getSkEmpleado();
        } else {
            return null;
        }
    }

    public void setSupervisorIdDelegate(Integer id) {
        this.ccSupervisor = new CcEmpleado();
        this.ccSupervisor.setSkEmpleado(id);
    }

    public String getSupervisorStDescripcionDelegate() {
        if (ccSupervisor != null) {
            return ccSupervisor.getStNombreCompleto();
        } else {
            return "";
        }
    }

    public String getSupervisorSelect2Delegate() {
        return String.valueOf(getSupervisorIdDelegate())
                + "|"
                + getSupervisorStDescripcionDelegate();
    }
}
