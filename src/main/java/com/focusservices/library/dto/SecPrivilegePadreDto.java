package com.focusservices.library.dto;


import java.io.Serializable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.*;
import org.hibernate.annotations.Subselect;

@Subselect("""
select r.SK_ROL                                                  as sk_rol
		, ph.sk_privilege											 as sk_privilege
		, ph.cd_privilege + ' - ' + ph.ST_DESCRIPTION			     as nombre_privilegio
		, pp.sk_privilege											 as sk_privilege_padre
		, convert(varchar(30), pp.cd_privilege) + ' - ' + convert(varchar(300), pp.ST_DESCRIPTION)  as nombre_privilegio_padre
		, convert(varchar(30), ph.fk_privilege_group) + '-' + convert(varchar(30), ph.sk_privilege) as codigo_orden
	, rp.SK_ROLE_PRIVILEGE										as sk_role_privilege
from CC_ROL r
cross join sec_privilege                    ph
join sec_privilege	                        pp on (pp.sk_privilege = ph.fk_privilege_group and ph.FK_PRIVILEGE_GROUP is not null)
left join SEC_ROLE_PRIVILEGE rp on (rp.FK_ROL = r.SK_ROL and ph.SK_PRIVILEGE = rp.FK_PRIVILEGE)
""")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SecPrivilegePadreDto implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(fetch = FetchType.EAGER)
    @EqualsAndHashCode.Include
    @Column(name = "codigo_orden" )
    @Size(max = 61, message = "El campo codigoOrden excede la longitud permitida")
    private String codigoOrden; 

    @Column(name = "sk_privilege" )
    @NotNull(message = "No puede estar vacio el campo skPrivilege")
    private Integer skPrivilege; 

    @Column(name = "nombre_privilegio" )
    @NotBlank(message = "No puede estar vacio el campo nombrePrivilegio")
    private String nombrePrivilegio; 

    @Column(name = "sk_privilege_padre" )
    private Integer skPrivilegePadre; 

    @Column(name = "nombre_privilegio_padre" )
    private String nombrePrivilegioPadre; 

    @Column(name = "sk_rol" )
    private Integer skRol;

    @Column(name = "sk_role_privilege" )
    private Integer skRolePrivilege; 

    public boolean isAsignado() {
        return skRolePrivilege != null;
    }


    // delegates de ids
}
