package com.focusservices.library.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.domain.CcRol;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;
import com.focusservices.library.domain.SecPrivilege;
import com.focusservices.library.dto.SecPrivilegePadreDto;

public interface CcRolService {
    Optional<CcRol> findById(Integer skRol);
    Optional<CcRol> findBySkRol(Integer skRol);
    ServiceResponse saveValidated(CcRol ccRol);
    ServiceResponse delete(Integer skRol);
    List<CcRol> findAll();
	
	// metodos para obtener data como lista
    Slice<CcRol> getList(Integer page, Integer rows);
    Slice<CcRol> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<CcRol> findAll(DataTablesInput input);

    DataTablesOutput<SecPrivilegePadreDto> findPrivilegesBySkRol(DataTablesInput input, Integer skRol);

    ServiceResponse asociarPrivilegio(Integer skRol, Integer skPrivilege);

    ServiceResponse eliminarPrivilegio(Integer skRol, Integer skPrivilege);

    ServiceResponse activarRol(Integer skRol);

    ServiceResponse inactivarRol(Integer skRol);

    List<SecPrivilege> getPrivilegiosDeRol(Integer fkRol);
}
