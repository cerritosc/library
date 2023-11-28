package com.focusservices.library.service;

import com.focusservices.library.domain.CcRol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.focusservices.library.repository.CcRolRepository;
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
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import com.focusservices.library.commons.datatables.specificationbuilder.GenericSpecificationBuilder;
import com.focusservices.library.domain.SecPrivilege;
import com.focusservices.library.domain.SecRolePrivilege;
import com.focusservices.library.dto.SecPrivilegePadreDto;
import com.focusservices.library.repository.dto.SecPrivilegePadreDtoRepository;

@Slf4j
@Service
@Transactional
public class CcRolServiceImpl implements CcRolService {

    @Autowired
    private CcRolRepository ccRolRepository;
    
    @Autowired
    private SecPrivilegePadreDtoRepository secPrivilegePadreDtoRepository;
    
    @Autowired
    private SecRolePrivilegeService secRolePrivilegeService;
    
    @Autowired
    private SecPrivilegeService secPrivilegeService;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<CcRol> findById(Integer skRol) {
            return ccRolRepository.findById(skRol);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CcRol> findBySkRol(Integer skRol) {
            return ccRolRepository.findBySkRol(skRol);
    }

    @Override
    public ServiceResponse saveValidated(CcRol ccRol) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            CcRol savedCcRol = ccRolRepository.save(ccRol);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedCcRol);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer skRol) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            CcRol ccRol = findById(skRol)
                    .orElseThrow(() -> new EntidadNoEncontradaException(skRol.toString()));
            ccRolRepository.delete(ccRol);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<CcRol> findAll() {
        return ccRolRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<CcRol> getList(Integer page, Integer rows) {
            return ccRolRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<CcRol> getListByQ(String q, Pageable page) {
            return ccRolRepository.findByStDescripcionIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<CcRol> findAll(DataTablesInput input) {
            return ccRolRepository.findAll(input);
    }

    @Override
    public DataTablesOutput<SecPrivilegePadreDto> findPrivilegesBySkRol(DataTablesInput input, Integer skRol) {
        Specification<SecPrivilegePadreDto> specification = GenericSpecificationBuilder.of(SecPrivilegePadreDto.class)
                .custom(GenericSpecificationBuilder.of(SecPrivilegePadreDto.class)
                        .or()
                        .equals("skRol", skRol)
                        .isNull("skRol")
                        .build()
                ).build();

        return secPrivilegePadreDtoRepository.findAll(input, null, specification);
    }

    @Override
    public ServiceResponse asociarPrivilegio(Integer skRol, Integer skPrivilege) {
        // busco el privilegio
        Optional<SecPrivilege> optionalPrivilegio = secPrivilegeService.findById(skPrivilege);
        if(optionalPrivilegio.isEmpty()) {
            return new ServiceResponse(false, "Privilegio no encontrado... ");
        }
        
        SecPrivilege privilegio = optionalPrivilegio.get();
        
        // obtengo todos los privilegios hijos de este padre
        Map<Integer, SecPrivilege> privilegiosHijos = secPrivilegeService.findHijosBySkPrivilegeAsMap(skPrivilege);
        
        // si hay privilegios hijos, se guardan estos primero
        if(!privilegiosHijos.isEmpty()) {
            // obtengo los rol/privilegios sin ser asignados
            List<Integer> privilegiosPendientes = secPrivilegePadreDtoRepository.findPrivilegiosFaltantes(skRol, skPrivilege);
            for(Integer idPrivilegioHijo : privilegiosPendientes) {
                SecPrivilege privilegioHijo = new SecPrivilege();
                privilegioHijo.setSkPrivilege(idPrivilegioHijo);
                
                // se crea la relacion rol/privilegio
                guardarNuevoPrivilegio(skRol, privilegioHijo);
            }
        }
        
        // se procede a guardar el privilegio original. Busco el privilegio en SecRolePrivilege
        Optional<SecRolePrivilege> privilegioPreviamenteAsociado = secRolePrivilegeService.findBySkRolAndSkPrivilege(skRol, skPrivilege);
        
        // solo si el rol no existe creo la asociacion
        if(privilegioPreviamenteAsociado.isEmpty()) {
            guardarNuevoPrivilegio(skRol, privilegio);
        }
        
        return new ServiceResponse(true, "Privilegio guardado de forma exitosa");
    }

    private void guardarNuevoPrivilegio(Integer skRol, SecPrivilege privilegio) {
        SecRolePrivilege rolPrivilegio = new SecRolePrivilege();
        rolPrivilegio.setCcRolIdDelegate(skRol);
        rolPrivilegio.setSecPrivilege(privilegio);
        
        secRolePrivilegeService.saveValidated(rolPrivilegio);
    }

    @Override
    public ServiceResponse eliminarPrivilegio(Integer skRol, Integer skPrivilege) {
        // obtengo todos los privilegios hijos de este padre
        Map<Integer, SecPrivilege> privilegiosHijos = secPrivilegeService.findHijosBySkPrivilegeAsMap(skPrivilege);
        
        // si hay privilegios hijos, se eliminan estos primero
        if(!privilegiosHijos.isEmpty()) {
            // obtengo los rol/privilegios asignados
            List<SecRolePrivilege> rolesPrivilegiosAsignados = secRolePrivilegeService.findPrivilegiosAsignados(skRol, privilegiosHijos.keySet());
            for(SecRolePrivilege rolPrivilegioHijo : rolesPrivilegiosAsignados) {
                secRolePrivilegeService.remove(rolPrivilegioHijo);
            }
        }
        
        // se procede a eliminar el privilegio original. Busco el privilegio en SecRolePrivilege
        Optional<SecRolePrivilege> optionalRolPrivilegio = secRolePrivilegeService.findBySkRolAndSkPrivilege(skRol, skPrivilege);
        
        // solo si el rol no existe creo la asociacion
        if(optionalRolPrivilegio.isPresent()) {
            secRolePrivilegeService.remove(optionalRolPrivilegio.get());
        }
        
        return new ServiceResponse(true, "Privilegio guardado de forma exitosa");
    }

    @Override
    public ServiceResponse activarRol(Integer skRol) {
        Boolean estadoDeseado = Boolean.TRUE;
        String mensajeExito = "Rol activado de forma exitosa";
        
        return cambiarEstadoRol(skRol, estadoDeseado, mensajeExito);
    }

    private ServiceResponse cambiarEstadoRol(Integer skRol, Boolean estadoDeseado, String mensajeExito) {
        Optional<CcRol> optionalRol = ccRolRepository.findById(skRol);
        if(optionalRol.isEmpty()) {
            return new ServiceResponse(false, "Rol no encontrado");
        }
        
        CcRol rol = optionalRol.get();
        rol.setBnEstado(estadoDeseado);
        ccRolRepository.save(rol);
        
        return new ServiceResponse(true, mensajeExito);
    }

    @Override
    public ServiceResponse inactivarRol(Integer skRol) {
        Boolean estadoDeseado = Boolean.FALSE;
        String mensajeExito = "Rol inactivado de forma exitosa";
        
        return cambiarEstadoRol(skRol, estadoDeseado, mensajeExito);
    }

    @Override
    public List<SecPrivilege> getPrivilegiosDeRol(Integer fkRol) {
        return ccRolRepository.getPrivilegiosDeRol(fkRol);
    }
}
