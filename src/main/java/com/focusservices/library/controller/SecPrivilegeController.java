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
import org.springframework.security.access.prepost.PreAuthorize;

import com.focusservices.library.domain.SecPrivilege;
import com.focusservices.library.service.SecPrivilegeService;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;
import com.focusservices.library.commons.Constants;
import com.focusservices.library.commons.S2;
import com.focusservices.library.commons.S2Response;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.commons.ValidadorHttp;
import lombok.extern.slf4j.Slf4j;
import com.focusservices.library.commons.S2Utils;


@Slf4j
@Controller
@RequestMapping("/secPrivilege")
public class SecPrivilegeController {
	
    private static final String HAS_AUTHORITY_READ = "hasAuthority('OP_READ_PRIVILEGE')";
    private static final String HAS_AUTHORITY_EDIT = "hasAuthority('OP_EDIT_PRIVILEGE')";
    private static final String HAS_AUTHORITY_DELETE = "hasAuthority('OP_DELETE_PRIVILEGE')";
    
    private static final String REDIRECT_SEC_PRIVILEGE = "redirect:/secPrivilege/";
    private static final String SEC_PRIVILEGE = "secPrivilege";
	
    @Autowired
    private SecPrivilegeService secPrivilegeService;
	
    
    @GetMapping(value = {"/", ""})
    public String indexSecPrivilege() {
            return "pages/secPrivilege/list";
    }

    
    @GetMapping("/list")
    public @ResponseBody
    DataTablesOutput<SecPrivilege> listSecPrivilege(@Valid DataTablesInput input) {
        return secPrivilegeService.findAll(input);
    }

    
    @GetMapping("/form")
    public String formSecPrivilege(@RequestParam(required = false) Integer skPrivilege, Model model) {
        if (!model.containsAttribute(SEC_PRIVILEGE)) {
            SecPrivilege secPrivilege = new SecPrivilege();
            if (skPrivilege != null) {
                Optional<SecPrivilege> optSecPrivilege = secPrivilegeService.findById(skPrivilege);
                if (!optSecPrivilege.isPresent()) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "SecPrivilege Not Found");
                }
                secPrivilege = optSecPrivilege.get();
            }
            model.addAttribute(SEC_PRIVILEGE, secPrivilege);
        }
        return "pages/secPrivilege/form";
    }

    
    @PostMapping("/save")
    public String saveEntity(@Valid SecPrivilege secPrivilege
            , BindingResult bdResult
            , RedirectAttributes atts
        ) {
        String redirectTo = Constants.REDIRECT_FORM;
        String[] parametrosAExcluir = new String[]{
            ""
        };
        if (ValidadorHttp.isPeticionCorrectaExcluyendoCampos(bdResult, parametrosAExcluir)) {
            ServiceResponse serviceResponse = secPrivilegeService.saveValidated(secPrivilege);
            atts.addFlashAttribute(Constants.SERVICE_RESPONSE_NAME, serviceResponse);
            redirectTo = serviceResponse.isSuccess() ? REDIRECT_SEC_PRIVILEGE : redirectTo;
        }
        atts.addFlashAttribute(SEC_PRIVILEGE, secPrivilege);
        atts.addFlashAttribute(BindingResult.class.getCanonicalName() + ".secPrivilege", bdResult);
        return redirectTo;
    }

    
    @PostMapping(value = {"/delete"})
    public @ResponseBody ServiceResponse deleteEntity(@RequestParam(value="skPrivilege", required = false) Integer skPrivilege){
        return secPrivilegeService.delete(skPrivilege);
    }

    
    @GetMapping(value = {"/cboFilterS2"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> s2(
            @RequestParam(value = "q", required = false, defaultValue = "") String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows) {
        return S2Utils.procesarPeticion(
                () -> secPrivilegeService.getListByQ(q, PageRequest.of(page - 1, rows))
                , entrada -> new S2(entrada.getSkPrivilege().toString(), entrada.getCdprivilege() + " - " + entrada.getStdescription())
                , rows
        );
    }
    
    
    @GetMapping(value = {"/cboFilterS2Padre"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> s2Padre(
            @RequestParam(value = "q", required = false, defaultValue = "") String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows) {
        return S2Utils.procesarPeticion(
                () -> secPrivilegeService.getListByQPadre(q, PageRequest.of(page - 1, rows))
                , entrada -> new S2(entrada.getSkPrivilege().toString(), entrada.getCdprivilege() + " - " + entrada.getStdescription())
                , rows
        );
    }
}
