package com.focusservices.library.service;

import com.focusservices.library.domain.CcEmpleado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.focusservices.library.repository.CcEmpleadoRepository;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class CcEmpleadoServiceImpl implements CcEmpleadoService {

    @Autowired
    private CcEmpleadoRepository ccEmpleadoRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<CcEmpleado> findById(Integer skEmpleado) {
            return ccEmpleadoRepository.findById(skEmpleado);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CcEmpleado> findBySkEmpleado(Integer skEmpleado) {
            return ccEmpleadoRepository.findBySkEmpleado(skEmpleado);
    }

    @Override
    public ServiceResponse saveValidated(CcEmpleado ccEmpleado) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            CcEmpleado savedCcEmpleado = ccEmpleadoRepository.save(ccEmpleado);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedCcEmpleado);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer skEmpleado) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            CcEmpleado ccEmpleado = findById(skEmpleado)
                    .orElseThrow(() -> new EntidadNoEncontradaException(skEmpleado.toString()));
            ccEmpleadoRepository.delete(ccEmpleado);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<CcEmpleado> findAll() {
        return ccEmpleadoRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<CcEmpleado> getList(Integer page, Integer rows) {
            return ccEmpleadoRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<CcEmpleado> getListByQ(String q, Pageable page) {
            return ccEmpleadoRepository.findByStNombreCompletoIgnoreCaseContaining(q, page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<CcEmpleado> findAll(DataTablesInput input) {
            return ccEmpleadoRepository.findAll(input);
    }

    @Override
    public ServiceResponse activarEmpleado(Integer skEmpleado) {
        Boolean estadoFinal = Boolean.TRUE;
        String mensajeExito = "Empleado activado de forma exitosa";
        
        return actualizarEstado(skEmpleado, estadoFinal, mensajeExito);
    }

    private ServiceResponse actualizarEstado(Integer skEmpleado, Boolean estadoFinal, String mensajeExito) {
        Optional<CcEmpleado> optionalEmpleado = ccEmpleadoRepository.findById(skEmpleado);
        if(optionalEmpleado.isEmpty()) {
            return new ServiceResponse(false, "Empleado no encontrado... ");
        }
        
        CcEmpleado empleado = optionalEmpleado.get();
        empleado.setBnEstado(estadoFinal);
        ccEmpleadoRepository.save(empleado);
        
        return new ServiceResponse(true, mensajeExito);
    }

    @Override
    public ServiceResponse inactivarEmpleado(Integer skEmpleado) {
        return actualizarEstado(skEmpleado, Boolean.FALSE, "Empleado inactivado de forma exitosa");
    }

    @Override
    public Slice<CcEmpleado> findEmpleadoSinUsuario(Integer skEmpleado, String q, Pageable pagina) {
        return ccEmpleadoRepository.findEmpleadoSinUsuario(skEmpleado, q, pagina);
    }
    
    //@Override
    public Slice<CcEmpleado> findEmpleadobyRol(String cdRol, Pageable pagina) {
        return ccEmpleadoRepository.findEmpleadobyRol(cdRol, pagina);
    }
    
    //empleados de back office y sin asignar
    public List<String[]> findEmpleadoBackOffice() {
        return ccEmpleadoRepository.findEmpleadosBackOffice();
    }
    
    @Transactional(readOnly = true)
	@Override
	public Slice<CcEmpleado> findEmpleadosBackOfficeActivos(String q, Pageable pagina) {
		return ccEmpleadoRepository.findEmpleadosBackOfficeActivos(q, pagina);
	}
        
     public List<String[]> findEmpleadosOrdenTrabajo(Integer skEmpleado) {
        return ccEmpleadoRepository.findEmpleadosOrdenTrabajo(skEmpleado);
    }   
     
     
    public List<String[]> findEmpleadoBySupervisor(Integer skEmpleado) {
        return ccEmpleadoRepository.findEmpleadoBySupervisor(skEmpleado);
    }      
}
