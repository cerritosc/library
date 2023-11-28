package com.focusservices.library.versionamiento;

import java.util.Map;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;

@Controller
@RequestMapping("/versionamiento")
public class VersionamientoController {

    private static final String HAS_AUTHORITY_VERSIONAMIENTO = "hasAuthority('OP_VERSIONAMIENTO')";

    @Autowired
    private VersionamientoService versionamientoService;

    @PreAuthorize(HAS_AUTHORITY_VERSIONAMIENTO)
    @RequestMapping(value = {"/", ""})
    public String indexVersionamiento() {
        return "pages/versionamiento/listTable";
    }

    @PreAuthorize(HAS_AUTHORITY_VERSIONAMIENTO)
    @GetMapping("/list")
    public @ResponseBody
    DataTablesOutput<MetadataTablaVersionada> getTablasVersionadas() {
        return versionamientoService.getTablasVersionadas();
    }

    @PreAuthorize(HAS_AUTHORITY_VERSIONAMIENTO)
    @GetMapping(value = "/log")
    public @ResponseBody
    DataTablesOutput<Map<String, Object>> getLogDeTabla(@Valid DataTablesInput input, String tabla) {
        return versionamientoService.getRegistrosVersionados(tabla, input);
    }

    @PreAuthorize(HAS_AUTHORITY_VERSIONAMIENTO)
    @GetMapping("/viewLog")
    public ModelAndView viewLog(String tabla) {
        ModelAndView modelAndView = new ModelAndView("pages/versionamiento/listLog");
        modelAndView.addObject("tabla", tabla);
        return modelAndView;
    }

    @PreAuthorize(HAS_AUTHORITY_VERSIONAMIENTO)
    @GetMapping("/logRegistro")
    public @ResponseBody
    DataTablesOutput<Map<String, Object>> getLogDeRegistro(
            @RequestParam String tabla
            , @RequestParam Integer id
            , @Valid DataTablesInput input
    ) {
        return versionamientoService.getHistorialDeRegistro(tabla, id, input);
    }

    @PreAuthorize(HAS_AUTHORITY_VERSIONAMIENTO)
    @GetMapping("/tabla/metadata")
    public @ResponseBody
    ServiceResponse getInformacionTabla(@RequestParam String tabla) {
        return versionamientoService.getMetadataTabla(tabla);
    }
}
