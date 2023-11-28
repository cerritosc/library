package com.focusservices.library.service;

import com.focusservices.library.domain.SecRolePrivilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.focusservices.library.repository.SecRolePrivilegeRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.focusservices.library.commons.Constants;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;
import com.focusservices.library.commons.exception.EntidadNoEncontradaException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SecRolePrivilegeServiceImpl implements SecRolePrivilegeService {

    @Autowired
    private SecRolePrivilegeRepository secRolePrivilegeRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<SecRolePrivilege> findById(Integer skRolePrivilege) {
            return secRolePrivilegeRepository.findById(skRolePrivilege);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SecRolePrivilege> findBySkRolePrivilege(Integer skRolePrivilege) {
            return secRolePrivilegeRepository.findBySkRolePrivilege(skRolePrivilege);
    }

    @Override
    public ServiceResponse saveValidated(SecRolePrivilege secRolePrivilege) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            SecRolePrivilege savedSecRolePrivilege = secRolePrivilegeRepository.save(secRolePrivilege);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedSecRolePrivilege);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer skRolePrivilege) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            SecRolePrivilege secRolePrivilege = findById(skRolePrivilege)
                    .orElseThrow(() -> new EntidadNoEncontradaException(skRolePrivilege.toString()));
            secRolePrivilegeRepository.delete(secRolePrivilege);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<SecRolePrivilege> findAll() {
        return secRolePrivilegeRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<SecRolePrivilege> getList(Integer page, Integer rows) {
            return secRolePrivilegeRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<SecRolePrivilege> getListByQ(String q, Pageable page) {
            return secRolePrivilegeRepository.findBySkRolePrivilegeIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<SecRolePrivilege> findAll(DataTablesInput input) {
            return secRolePrivilegeRepository.findAll(input);
    }

    @Override
    public Optional<SecRolePrivilege> findBySkRolAndSkPrivilege(Integer skRol, Integer skPrivilege) {
        return secRolePrivilegeRepository.findBySkRolAndSkPrivilege(skRol, skPrivilege);
    }

    @Override
    public void remove(SecRolePrivilege rolPrivilegioHijo) {
        secRolePrivilegeRepository.delete(rolPrivilegioHijo);
    }

    @Override
    public List<SecRolePrivilege> findPrivilegiosAsignados(Integer skRol, Set<Integer> privilegiosHijos) {
        return secRolePrivilegeRepository.findPrivilegiosAsignados(skRol, privilegiosHijos);
    }
}
