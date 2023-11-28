package com.focusservices.library.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.apache.commons.lang3.StringUtils;
import com.focusservices.library.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.focusservices.library.repository.BookRepository;
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
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<Book> findById(Integer id) {
            return bookRepository.findById(id);
    }


    @Override
    public ServiceResponse saveValidated(Book book) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            Book savedBook = bookRepository.save(book);
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedBook);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer id) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            Book book = findById(id)
                    .orElseThrow(() -> new EntidadNoEncontradaException(id.toString()));
            bookRepository.delete(book);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<Book> getList(Integer page, Integer rows) {
            return bookRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<Book> getListByQ(String q, Pageable page) {
        if(!StringUtils.isBlank(q)) {
            return bookRepository.findByGenreIgnoreCaseContaining(q, page);
        }
        else return bookRepository.findAll(page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<Book> findAll(DataTablesInput input) {
            return bookRepository.findAll(input);
    }
}
