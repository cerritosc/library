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

import com.focusservices.library.domain.Book;
import com.focusservices.library.service.BookService;
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
@RequestMapping("/book")
public class BookController {
	
    private static final String HAS_AUTHORITY_USER = "hasAuthority('')";
	
    @Autowired
    private BookService bookService;
	
    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping(value = {"/", ""})
    public String indexBook() {
            return "pages/book/list";
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)	
    @GetMapping("/list")
    public @ResponseBody
    DataTablesOutput<Book> listBook(@Valid DataTablesInput input) {
        return bookService.findAll(input);
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @GetMapping("/form")
    public @ResponseBody Book formBook(@RequestParam(required = false) Integer id, Model model) {
        if(id != null){
            Optional<Book> bookOptional = bookService.findById(id);
            if(bookOptional.isPresent()){
                return bookOptional.get();
            }
        }
        return null;
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @PostMapping("/save")
    public @ResponseBody ServiceResponse saveEntity(@Valid Book book
            , BindingResult bdResult
        ) {
        ServiceResponse serviceResponse = new ServiceResponse();
        String[] parametrosAExcluir = new String[]{
            ""
        };
        if (ValidadorHttp.isPeticionCorrectaExcluyendoCampos(bdResult, parametrosAExcluir)) {
            serviceResponse = bookService.saveValidated(book);
        } else{
             serviceResponse.setSuccess(false);
             serviceResponse.setMessage("Error al guardar el registro, causas: "+ bdResult.getAllErrors());
        }
        return serviceResponse;
    }

    //@PreAuthorize(HAS_AUTHORITY_USER)
    @PostMapping("/delete")
    public @ResponseBody ServiceResponse deleteEntity(@RequestParam(value="id", required = false) Integer id){
        ServiceResponse serviceResponse = bookService.delete(id);
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
                () -> bookService.getListByQ(q, pagina)
                , entrada -> new S2(entrada.getId().toString()
                        , entrada.getDescriptionForlist()
                        , entrada)
                , rows
        );
    }
}
