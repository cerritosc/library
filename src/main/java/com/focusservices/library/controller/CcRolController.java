package com.focusservices.library.controller;

import java.util.Optional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.focusservices.library.domain.CcRol;
import com.focusservices.library.service.CcRolService;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;
import com.focusservices.library.commons.Constants;
import com.focusservices.library.commons.S2;
import com.focusservices.library.commons.S2Response;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.commons.ValidadorHttp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import com.focusservices.library.commons.S2Utils;
import com.focusservices.library.dto.SecPrivilegePadreDto;


@Slf4j
@Controller
@RequestMapping("/ccRol")
public class CcRolController {
	
    private static final String HAS_AUTHORITY_READ = "hasAuthority('OP_READ_ROL')";
    private static final String HAS_AUTHORITY_EDIT = "hasAuthority('OP_EDIT_ROL')";
    private static final String HAS_AUTHORITY_DELETE = "hasAuthority('OP_DELETE_ROL')";
    private static final String HAS_AUTHORITY_ACTIVATE = "hasAuthority('OP_ACTIVATE_ROL')";
    private static final String HAS_AUTHORITY_ASSIGN_PRIVILEGE = "hasAuthority('OP_ASSIGN_PRIVILEGE_TO_ROL')";
    private static final String REDIRECT_CC_ROL = "redirect:/ccRol/";
    private static final String CC_ROL = "ccRol";
	
    @Autowired
    private CcRolService ccRolService;
    
    
    @GetMapping(value = {"/", ""})
    public String indexCcRol() {
            return "pages/ccRol/list";
    }

    
    @GetMapping("/list")
    public @ResponseBody
    DataTablesOutput<CcRol> listCcRol(@Valid DataTablesInput input) {
        return ccRolService.findAll(input);
    }

    
    @GetMapping("/form")
    public String formCcRol(@RequestParam(required = false) Integer skRol, Model model) {
        if (!model.containsAttribute(CC_ROL)) {
            CcRol ccRol = new CcRol();
            ccRol.setBnEstado(false);
            if (skRol != null) {
                Optional<CcRol> optCcRol = ccRolService.findById(skRol);
                if (!optCcRol.isPresent()) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "CcRol Not Found");
                }
                ccRol = optCcRol.get();
            }
            model.addAttribute(CC_ROL, ccRol);
        }
        return "pages/ccRol/form";
    }

    
    @PostMapping("/save")
    public String saveEntity(@Valid CcRol ccRol
            , BindingResult bdResult
            , RedirectAttributes atts
        ) {
        String redirectTo = Constants.REDIRECT_FORM;
        String[] parametrosAExcluir = new String[]{
            ""
        };
        if (ValidadorHttp.isPeticionCorrectaExcluyendoCampos(bdResult, parametrosAExcluir)) {
            ServiceResponse serviceResponse = ccRolService.saveValidated(ccRol);
            atts.addFlashAttribute(Constants.SERVICE_RESPONSE_NAME, serviceResponse);
            redirectTo = serviceResponse.isSuccess() ? REDIRECT_CC_ROL : redirectTo;
        }
        atts.addFlashAttribute(CC_ROL, ccRol);
        atts.addFlashAttribute(BindingResult.class.getCanonicalName() + ".ccRol", bdResult);
        return redirectTo;
    }

    
    @PostMapping(value = {"/delete"})
    public @ResponseBody ServiceResponse deleteEntity(@RequestParam(value="skRol", required = false) Integer skRol){
        return ccRolService.delete(skRol);
    }
    
    
    @PostMapping("/activar")
    public @ResponseBody ServiceResponse activarRol(
            @RequestParam Integer skRol) {
        return ccRolService.activarRol(skRol);
    }
    
    
    @PostMapping("/inactivar")
    public @ResponseBody ServiceResponse inactivarRol(
            @RequestParam Integer skRol) {
        return ccRolService.inactivarRol(skRol);
    }
    
    
    @GetMapping("/privilege")
    public String mostrarPrivilegios(@RequestParam Integer skRol, Model modelo) {
        Optional<CcRol> optionalRol = ccRolService.findById(skRol);
        if(optionalRol.isPresent()) {
            modelo.addAttribute("role", optionalRol.get());
            return "pages/ccRol/assignPrivilege";
        }
        else 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Privilegio no encontrado.");
    }
    
    
    @GetMapping(value = "/privilege/list")
    public @ResponseBody
    DataTablesOutput<SecPrivilegePadreDto> listCcRolPrivilege(@Valid DataTablesInput input
            , @RequestParam Integer skRol) {
        return ccRolService.findPrivilegesBySkRol(input, skRol);
    }
    
    
    @PostMapping("/privilege/add")
    public @ResponseBody ServiceResponse guardarPrivilegios(
            @RequestParam Integer skRol
            , @RequestParam Integer skPrivilege) {
        return ccRolService.asociarPrivilegio(skRol, skPrivilege);
    }
    
    
    @PostMapping("/privilege/remove")
    public @ResponseBody ServiceResponse eliminarPrivilegios(
            @RequestParam Integer skRol
            , @RequestParam Integer skPrivilege) {
        return ccRolService.eliminarPrivilegio(skRol, skPrivilege);
    }

    //
    @GetMapping(value = {"/cboFilterS2"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> s2(
            @RequestParam(value = "q", required = false, defaultValue = "") String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows) {
        Pageable pagina = PageRequest.of(page - 1, rows);
        return S2Utils.procesarPeticion(
                () -> ccRolService.getListByQ(q, pagina)
                , entrada -> new S2(entrada.getSkRol().toString(), entrada.getStDescripcion())
                , rows
        );
    }
}
