package com.focusservices.library.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.focusservices.library.domain.CcUsuario;
import com.focusservices.library.service.CcUsuarioService;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;
import com.focusservices.library.commons.Constants;
import com.focusservices.library.commons.S2;
import com.focusservices.library.commons.S2Response;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.commons.ValidadorHttp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;


@Slf4j
@Controller
@RequestMapping("/ccUsuario")
public class CcUsuarioController {
    
    private static final String HAS_AUTHORITY_READ = "hasAuthority('OP_READ_USUARIO')";
    private static final String HAS_AUTHORITY_EDIT = "hasAuthority('OP_EDIT_USUARIO')";
    private static final String HAS_AUTHORITY_DELETE = "hasAuthority('OP_DELETE_USUARIO')";
    private static final String HAS_AUTHORITY_ACTIVATE = "hasAuthority('OP_ACTIVATE_USUARIO')";
	
    private static final String REDIRECT_CC_USUARIO = "redirect:/ccUsuario/";
    private static final String CC_USUARIO = "ccUsuario";
	
    @Autowired
    private CcUsuarioService ccUsuarioService;
	
    
    @GetMapping(value = {"/", ""})
    public String indexCcUsuario() {
            return "pages/ccUsuario/list";
    }

    
    @GetMapping("/list")
    public @ResponseBody
    DataTablesOutput<CcUsuario> listCcUsuario(@Valid DataTablesInput input) {
        return ccUsuarioService.findAll(input);
    }

    
    @GetMapping("/form")
    public String formCcUsuario(@RequestParam(required = false) Integer skUsuario, Model model) {
        if (!model.containsAttribute(CC_USUARIO)) {
            CcUsuario ccUsuario = new CcUsuario();
            if (skUsuario != null) {
                Optional<CcUsuario> optCcUsuario = ccUsuarioService.findById(skUsuario);
                if (!optCcUsuario.isPresent()) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "CcUsuario Not Found");
                }
                ccUsuario = optCcUsuario.get();
            }
            model.addAttribute(CC_USUARIO, ccUsuario);
        }
        return "pages/ccUsuario/form";
    }

    
    @PostMapping("/save")
    public String saveEntity(@Valid CcUsuario ccUsuario
            , BindingResult bdResult
            , RedirectAttributes atts
        ) {
        String redirectTo = Constants.REDIRECT_FORM;
        String[] parametrosAExcluir = new String[]{
            ""
        };
        if (ValidadorHttp.isPeticionCorrectaExcluyendoCampos(bdResult, parametrosAExcluir)) {
            ServiceResponse serviceResponse = ccUsuarioService.saveValidated(ccUsuario);
            atts.addFlashAttribute(Constants.SERVICE_RESPONSE_NAME, serviceResponse);
            redirectTo = serviceResponse.isSuccess() ? REDIRECT_CC_USUARIO : redirectTo;
        }
        atts.addFlashAttribute(CC_USUARIO, ccUsuario);
        atts.addFlashAttribute(BindingResult.class.getCanonicalName() + ".ccUsuario", bdResult);
        return redirectTo;
    }

    
    @PostMapping(value = {"/delete"})
    public @ResponseBody ServiceResponse deleteEntity(@RequestParam(value="skUsuario", required = false) Integer skUsuario){
        return ccUsuarioService.delete(skUsuario);
    }
    
    
    @PostMapping(value = {"/activar"})
    public @ResponseBody ServiceResponse activarUsuario(@RequestParam Integer skUsuario){
        return ccUsuarioService.activarUsuario(skUsuario);
    }
    
    
    @PostMapping(value = {"/inactivar"})
    public @ResponseBody ServiceResponse desactivarUsuario(@RequestParam Integer skUsuario){
        return ccUsuarioService.desactivarUsuario(skUsuario);
    }

    //
    @GetMapping(value = {"/cboFilterS2"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> s2(
            @RequestParam(value = "q", required = false, defaultValue = "") String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows) {
        S2Response<S2> response;
        if (rows != 0) {
            Slice<CcUsuario> list;
            if (q == null || q.equals("")) {
                list = ccUsuarioService.getList(page, rows);
            } else {
                list = ccUsuarioService.getListByQ(q, PageRequest.of(page - 1, rows));
            }
            List<S2> results = new ArrayList<>();
            list.getContent().stream().map(u -> new S2(u.getSkUsuario().toString(), u.getCdUsuario(), null)).forEachOrdered(results::add);
            response = new S2Response<>(results, list.hasNext());
        } else {
            response = new S2Response<>();
        }
        return response;
    }
}
