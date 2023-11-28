package com.focusservices.library.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.focusservices.library.domain.Loan;
import com.focusservices.library.service.LoanService;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;
import com.focusservices.library.commons.Constants;
import com.focusservices.library.commons.S2;
import com.focusservices.library.commons.S2Response;
import com.focusservices.library.commons.S2Utils;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.commons.ValidadorHttp;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/loan")
public class LoanController {
	
    private static final String HAS_AUTHORITY_USER = "hasAuthority('')";
	
    @Autowired
    private LoanService loanService;
	
    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping(value = {"/", ""})
    public String indexLoan() {
            return "pages/loan/list";
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)	
    @GetMapping("/list")
    public @ResponseBody
    DataTablesOutput<Loan> listLoan(@Valid DataTablesInput input) {
        return loanService.findAll(input);
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping("/form")
    public @ResponseBody Loan formLoan(@RequestParam(required = false) Integer id, Model model) {
        if(id != null){
            Optional<Loan> loanOptional = loanService.findById(id);
            if(loanOptional.isPresent()){
                return loanOptional.get();
            }
        }
        return null;
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @PostMapping("/save")
    public @ResponseBody ServiceResponse saveEntity(@Valid Loan loan
            , BindingResult bdResult
        ) {
        ServiceResponse serviceResponse = new ServiceResponse();
        String[] parametrosAExcluir = new String[]{
            ""
        };
        if (ValidadorHttp.isPeticionCorrectaExcluyendoCampos(bdResult, parametrosAExcluir)) {
            serviceResponse = loanService.saveValidated(loan);
        } else{
             serviceResponse.setSuccess(false);
             serviceResponse.setMessage("Error al guardar el registro, causas: "+ bdResult.getAllErrors());
        }
        return serviceResponse;
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @PostMapping("/delete")
    public @ResponseBody ServiceResponse deleteEntity(@RequestParam(value="id", required = false) Integer id){
        ServiceResponse serviceResponse = loanService.delete(id);
        return serviceResponse;
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping(value = {"/cboFilterS2"}, produces = Constants.APPLICATION_JSON)
    public @ResponseBody
    S2Response<S2> s2(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "rows", required = false) Integer rows) {
        PageRequest pagina = PageRequest.of(page - 1, rows);
        return S2Utils.procesarPeticion(
                () -> loanService.getListByQ(q, pagina)
                , entrada -> new S2(entrada.getId().toString()
                        , entrada.getId().toString()
                        , entrada)
                , rows
        );
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @PostMapping("/return")
    public @ResponseBody ServiceResponse returnBook(@Valid Loan loan
            , BindingResult bdResult){
    	ServiceResponse serviceResponse = new ServiceResponse();
        String[] parametrosAExcluir = new String[]{
            ""
        };
        if (ValidadorHttp.isPeticionCorrectaExcluyendoCampos(bdResult, parametrosAExcluir)) {
            serviceResponse = loanService.returnBook(loan);
        } else{
             serviceResponse.setSuccess(false);
             serviceResponse.setMessage("Error al guardar el registro, causas: "+ bdResult.getAllErrors());
        }
        return serviceResponse;
    }
}
