package com.focusservices.library.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.domain.SecRolePrivilege;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;

public interface SecRolePrivilegeService {
    Optional<SecRolePrivilege> findById(Integer skRolePrivilege);
    Optional<SecRolePrivilege> findBySkRolePrivilege(Integer skRolePrivilege);
    ServiceResponse saveValidated(SecRolePrivilege secRolePrivilege);
    ServiceResponse delete(Integer skRolePrivilege);
    List<SecRolePrivilege> findAll();
	
	// metodos para obtener data como lista
    Slice<SecRolePrivilege> getList(Integer page, Integer rows);
    Slice<SecRolePrivilege> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<SecRolePrivilege> findAll(DataTablesInput input);

    Optional<SecRolePrivilege> findBySkRolAndSkPrivilege(Integer skRol, Integer skPrivilege);

    void remove(SecRolePrivilege rolPrivilegioHijo);

    List<SecRolePrivilege> findPrivilegiosAsignados(Integer skRol, Set<Integer> privilegiosHijos);
}
