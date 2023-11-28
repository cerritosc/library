package com.focusservices.library.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import com.focusservices.library.commons.ServiceResponse;
import com.focusservices.library.domain.Book;
import com.focusservices.library.commons.datatables.mapping.DataTablesInput;
import com.focusservices.library.commons.datatables.mapping.DataTablesOutput;

public interface BookService {
    Optional<Book> findById(Integer id);
    ServiceResponse saveValidated(Book book);
    ServiceResponse delete(Integer id);
    List<Book> findAll();
	
	// metodos para obtener data como lista
    Slice<Book> getList(Integer page, Integer rows);
    Slice<Book> getListByQ(String q, Pageable page);
	
    // metodos para DatatTable
    DataTablesOutput<Book> findAll(DataTablesInput input);
}
