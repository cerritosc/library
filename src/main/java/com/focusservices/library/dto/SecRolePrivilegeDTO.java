package com.focusservices.library.dto;

import lombok.Data;
import org.hibernate.annotations.Subselect;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Data
@Entity
@Subselect("SELECT a.FK_ROLE skRole, b.SK_PRIVILEGE skPrivilege, b.C_PRIVILEGE cprivilege, b.S_DESCRIPTION sdescription FROM SEC_ROLE_PRIVILEGE a JOIN SEC_PRIVILEGE b ON a.FK_PRIVILEGE = b.SK_PRIVILEGE")
public class SecRolePrivilegeDTO {

    @Id
    private Integer skPrivilege;
    private Integer skRole;
    private String cprivilege;
    private String sdescription;
}
