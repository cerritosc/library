package com.focusservices.library.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
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
@Table(name = "SEC_ROLE_PRIVILEGE")
//@TablaVersionada(nombre = "secRolePrivilege", etiqueta = "Privilegio rol")
public class SecRolePrivilege implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "SK_ROLE_PRIVILEGE")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ROLE_PRIVILEGE")
    @SequenceGenerator(name = "SEQ_ROLE_PRIVILEGE", sequenceName = "SEQ_ROLE_PRIVILEGE", allocationSize = 1)
    private Integer skRolePrivilege; 

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
    @Size(max = 255, message = "El campo stCreaUsuario excede la longitud permitida")
    @CreatedBy
    private String stCreaUsuario; 

    @Column(name = "ST_MOD_USUARIO" )
    @Size(max = 255, message = "El campo stModUsuario excede la longitud permitida")
    @LastModifiedBy
    private String stModUsuario; 




        @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "FK_PRIVILEGE", referencedColumnName = "SK_PRIVILEGE")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private SecPrivilege secPrivilege; 

        @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "FK_ROL", referencedColumnName = "SK_ROL")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private CcRol ccRol; 



    // delegates de ids
    public Integer getSecPrivilegeSkPrivilegeDelegate() {
        if(secPrivilege != null) {
            return secPrivilege.getSkPrivilege();
        }
        else return null;
    } 
    
    public String getSecPrivilegeStDescripcionDelegate() {
        if(secPrivilege != null) {
            return secPrivilege.getSkPrivilege().toString();
        }
        else return "";
    }

    public String getSecPrivilegeSelect2Delegate() {
        return String.valueOf(getSecPrivilegeSkPrivilegeDelegate())
            + "|"
            + getSecPrivilegeStDescripcionDelegate();
    }
    public Integer getCcRolSkRolDelegate() {
        if(ccRol != null) {
            return ccRol.getSkRol();
        }
        else return null;
    } 
    
    public String getCcRolStDescripcionDelegate() {
        if(ccRol != null) {
            return ccRol.getSkRol().toString();
        }
        else return "";
    }

    public String getCcRolSelect2Delegate() {
        return String.valueOf(getCcRolSkRolDelegate())
            + "|"
            + getCcRolStDescripcionDelegate();
    }

    public void setCcRolIdDelegate(Integer skRol) {
        this.ccRol = new CcRol();
        this.ccRol.setSkRol(skRol);
    }
}
