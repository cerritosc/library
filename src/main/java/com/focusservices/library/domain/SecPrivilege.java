package com.focusservices.library.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "SEC_PRIVILEGE")
public class SecPrivilege implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "SK_PRIVILEGE")
    @EqualsAndHashCode.Include
    @JsonView
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRIVILEGE")
    @SequenceGenerator(name = "SEQ_PRIVILEGE", sequenceName = "SEQ_PRIVILEGE", allocationSize = 1)
    private Integer skPrivilege; 

    @Column(name = "CD_PRIVILEGE" )
    @NotBlank(message = "No puede estar vacio el campo cprivilege")
    @Size(max = 32, message = "El campo cprivilege excede la longitud permitida")
    private String cdprivilege; 

    @Column(name = "ST_DESCRIPTION" )
    @NotBlank(message = "No puede estar vacio el campo sdescription")
    @Size(max = 256, message = "El campo sdescription excede la longitud permitida")
    private String stdescription; 



    @OneToMany(mappedBy = "secPrivilege", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<SecRolePrivilege> secRolePrivilegees;

    @OneToMany(mappedBy = "secPrivilegePadre", cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter(onMethod = @__(@JsonIgnore))
    @ToString.Exclude
    private Set<SecPrivilege> secPrivilegeHijos;


    @Getter(onMethod = @__( @JsonIgnore))
    @JoinColumn(name = "FK_PRIVILEGE_GROUP", referencedColumnName = "SK_PRIVILEGE")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private SecPrivilege secPrivilegePadre; 



    // delegates de ids
    public Integer getSecPrivilegePadreIdDelegate() {
        if(secPrivilegePadre != null) {
            return secPrivilegePadre.getSkPrivilege();
        }
        else return null;
    } 
    
    public String getSecPrivilegePadreStDescripcionDelegate() {
        if(secPrivilegePadre != null) {
            return secPrivilegePadre.getCdprivilege() + " - " + secPrivilegePadre.getStdescription();
        }
        else return "";
    }

    public String getSecPrivilegePadreSelect2Delegate() {
        return String.valueOf(getSecPrivilegePadreIdDelegate())
            + "|"
            + getSecPrivilegePadreStDescripcionDelegate();
    }

    public void setSecPrivilegePadreIdDelegate(Integer idPadre) {
        this.secPrivilegePadre = new SecPrivilege();
        this.secPrivilegePadre.setSkPrivilege(idPadre);
    }
}
