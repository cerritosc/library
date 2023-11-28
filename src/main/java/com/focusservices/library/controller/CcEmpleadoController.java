package com.focusservices.library.controller;

import java.util.Optional;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.prepost.PreAuthorize;

import com.focusservices.library.domain.CcEmpleado;
import com.focusservices.library.service.CcEmpleadoService;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;
import com.focusservices.library.commons.Constants;
import com.focusservices.library.commons.S2;
import com.focusservices.library.commons.S2Response;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.commons.ValidadorHttp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import com.focusservices.library.commons.S2Utils;


@Slf4j
@Controller
@RequestMapping("/ccEmpleado")
public class CcEmpleadoController {
	
    private static final String HAS_AUTHORITY_READ = "hasAuthority('OP_READ_EMPLEADO')";
    private static final String HAS_AUTHORITY_EDIT = "hasAuthority('OP_EDIT_EMPLEADO')";
    private static final String HAS_AUTHORITY_DELETE = "hasAuthority('OP_DELETE_EMPLEADO')";
    private static final String HAS_AUTHORITY_ACTIVATE = "hasAuthority('OP_ACTIVATE_EMPLEADO')";
    
    private static final String REDIRECT_CC_EMPLEADO = "redirect:/ccEmpleado/";
    private static final String CC_EMPLEADO = "ccEmpleado";
	
    @Autowired
    private CcEmpleadoService ccEmpleadoService;
	
    
    @GetMapping(value = {"/", ""})
    public String indexCcEmpleado() {
            return "pages/ccEmpleado/list";
    }

    
    @GetMapping("/list")
    public @ResponseBody
    DataTablesOutput<CcEmpleado> listCcEmpleado(@Valid DataTablesInput input) {
        return ccEmpleadoService.findAll(input);
    }

    
    @GetMapping("/form")
    public String formCcEmpleado(@RequestParam(required = false) Integer skEmpleado, Model model) {
        if (!model.containsAttribute(CC_EMPLEADO)) {
            CcEmpleado ccEmpleado = new CcEmpleado();
            ccEmpleado.setBnEstado(Boolean.FALSE);
            if (skEmpleado != null) {
                Optional<CcEmpleado> optCcEmpleado = ccEmpleadoService.findById(skEmpleado);
                if (!optCcEmpleado.isPresent()) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "CcEmpleado Not Found");
                }
                ccEmpleado = optCcEmpleado.get();
            }
            model.addAttribute(CC_EMPLEADO, ccEmpleado);
        }
        return "pages/ccEmpleado/form";
    }

    
    @PostMapping("/save")
    public String saveEntity(@Valid CcEmpleado ccEmpleado
            , BindingResult bdResult
            , RedirectAttributes atts
        ) {
        String redirectTo = Constants.REDIRECT_FORM;
        String[] parametrosAExcluir = new String[]{
            ""
        };
        if (ValidadorHttp.isPeticionCorrectaExcluyendoCampos(bdResult, parametrosAExcluir)) {
            ServiceResponse serviceResponse = ccEmpleadoService.saveValidated(ccEmpleado);
            atts.addFlashAttribute(Constants.SERVICE_RESPONSE_NAME, serviceResponse);
            redirectTo = serviceResponse.isSuccess() ? REDIRECT_CC_EMPLEADO : redirectTo;
        }
        atts.addFlashAttribute(CC_EMPLEADO, ccEmpleado);
        atts.addFlashAttribute(BindingResult.class.getCanonicalName() + ".ccEmpleado", bdResult);
        return redirectTo;
    }

    
    @PostMapping(value = {"/delete"})
    public @ResponseBody ServiceResponse deleteEntity(@RequestParam(value="skEmpleado", required = false) Integer skEmpleado){
        return ccEmpleadoService.delete(skEmpleado);
    }
    
    
    @PostMapping("/activar")
    public @ResponseBody ServiceResponse activarEmpleado(@RequestParam Integer skEmpleado){
        return ccEmpleadoService.activarEmpleado(skEmpleado);
    }
    
    
    @PostMapping("/inactivar")
    public @ResponseBody ServiceResponse inactivarEmpleado(@RequestParam Integer skEmpleado){
        return ccEmpleadoService.inactivarEmpleado(skEmpleado);
    }

    @GetMapping(value = {"/cboFilterS2"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> s2(
            @RequestParam(value = "q", required = false, defaultValue = "") String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows) {
        Pageable pagina = PageRequest.of(page - 1, rows);
        return S2Utils.procesarPeticion(
                () -> ccEmpleadoService.getListByQ(q, pagina)
                , entrada -> new S2(entrada.getSkEmpleado().toString(), entrada.getStNombreCompleto())
                , rows
        );
    }
    
    
    @GetMapping(value = {"/s2/sinUsuario"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> getListadoEmpleadosSinUsuario(
            @RequestParam(value = "q", required = false, defaultValue = "") String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows,
            @RequestParam(required = false, defaultValue = "-1") Integer skEmpleado) {
        Pageable pagina = PageRequest.of(page - 1, rows);
        return S2Utils.procesarPeticion(
                () -> ccEmpleadoService.findEmpleadoSinUsuario(skEmpleado, q, pagina)
                , entrada -> new S2(entrada.getSkEmpleado().toString(), entrada.getStNombreCompleto())
                , rows
        );
    }
    
    
     
    @GetMapping(value = {"/cboEmpleadoRol"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> getListadoEmpleadosRol(
            @RequestParam(value = "q", required = false, defaultValue = "") String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows,
             @RequestParam(value = "cdRol", required = false) String cdRol) {
        Pageable pagina = PageRequest.of(page - 1, rows);
               
        return S2Utils.procesarPeticion(   
                () -> ccEmpleadoService.findEmpleadobyRol(cdRol, pagina)
                , entrada -> new S2(entrada.getSkEmpleado().toString(), entrada.getStNombreCompleto())
                , rows
        );
    }
    
    //los de back office
    //combo de numero de llamadas
     @GetMapping(value = {"/cboEmpleadoBackOffice"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> getListaBackOffice(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows) {
        S2Response<S2> response;
        List<S2> results = new ArrayList<>();
        //extrayendo lista de valores
       List<String[]> listEmpleados =  ccEmpleadoService.findEmpleadoBackOffice();
       if(!listEmpleados.isEmpty()){
           for(int i = 0; i < listEmpleados.size() ; i++){
                 results.add(  new S2(listEmpleados.get(i)[0] ,  listEmpleados.get(i)[1] ) );
           }
         
       }
        

        response = new S2Response<>(results, false);

        return response;
    }
    
     
    
     @GetMapping(value = {"cboFilterS2EmpleadosAgentesActivos"}, produces = Constants.APPLICATION_JSON)
     public @ResponseBody S2Response<S2> cboFilterS2EmpleadosAgentesActivos(
             @RequestParam(value = "q", required = false, defaultValue = "") String q,
             @RequestParam(value = "page", required = false) Integer page,
             @RequestParam(value = "rows", required = false) Integer rows) {
         return S2Utils.procesarPeticion(
                 () -> ccEmpleadoService.findEmpleadosBackOfficeActivos(q, PageRequest.of(page - 1, rows))
                 , entrada -> new S2(entrada.getSkEmpleado().toString(), entrada.getStNombreCompleto())
                 , rows
         );
     }
     
    
     
       @GetMapping(value = {"/cboEmpleadoOT"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> getListaEmpleadoOT(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows,
             @RequestParam(value = "skEmpleado", required = false) Integer skEmpleado) {
        S2Response<S2> response;
        List<S2> results = new ArrayList<>();
       
       List<String[]> listEmpleados =  ccEmpleadoService.findEmpleadosOrdenTrabajo(skEmpleado);
       if(!listEmpleados.isEmpty()){
           for(int i = 0; i < listEmpleados.size() ; i++){
                 results.add(  new S2(listEmpleados.get(i)[0] ,  listEmpleados.get(i)[1] ) );
           }
         
       }
        

        response = new S2Response<>(results, false);

        return response;
    }
    
    
       @GetMapping(value = {"/cboEmpleadoBySupervisor"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> getListaEmpleadoSupervisor(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows,
             @RequestParam(value = "skEmpleado", required = false) Integer skEmpleado) {
        S2Response<S2> response;
        List<S2> results = new ArrayList<>();
       
       List<String[]> listEmpleados =  ccEmpleadoService.findEmpleadoBySupervisor(skEmpleado);
       if(!listEmpleados.isEmpty()){
           for(int i = 0; i < listEmpleados.size() ; i++){
                 results.add(  new S2(listEmpleados.get(i)[0] ,  listEmpleados.get(i)[1] ) );
           }
         
       }
        

        response = new S2Response<>(results, false);

        return response;
    }

    
    
}
