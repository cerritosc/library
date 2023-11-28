package com.focusservices.library.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.domain.CcUsuario;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;

public interface CcUsuarioService {
    Optional<CcUsuario> findById(Integer skUsuario);
    Optional<CcUsuario> findBySkUsuario(Integer skUsuario);
    ServiceResponse saveValidated(CcUsuario ccUsuario);
    ServiceResponse delete(Integer skUsuario);
    List<CcUsuario> findAll();
	
	// metodos para obtener data como lista
    Slice<CcUsuario> getList(Integer page, Integer rows);
    Slice<CcUsuario> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<CcUsuario> findAll(DataTablesInput input);

    ServiceResponse activarUsuario(Integer skUsuario);

    ServiceResponse desactivarUsuario(Integer skUsuario);
}
