package com.focusservices.library.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.domain.Loan;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;

public interface LoanService {
    Optional<Loan> findById(Integer id);
    ServiceResponse saveValidated(Loan loan);
    ServiceResponse delete(Integer id);
    List<Loan> findAll();
	
	// metodos para obtener data como lista
    Slice<Loan> getList(Integer page, Integer rows);
    Slice<Loan> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<Loan> findAll(DataTablesInput input);
}
