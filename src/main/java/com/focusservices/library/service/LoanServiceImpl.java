package com.focusservices.library.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.apache.commons.lang3.StringUtils;

import com.focusservices.library.domain.Book;
import com.focusservices.library.domain.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.focusservices.library.repository.BookRepository;
import com.focusservices.library.repository.LoanRepository;
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
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Optional<Loan> findById(Integer id) {
            return loanRepository.findById(id);
    }

    @Override
    public ServiceResponse saveValidated(Loan loan) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            String resultMsg = Constants.MSG_GUARDADO_EXITOSO;
            boolean resultFlag = true;
            Book book = bookRepository.findById(loan.getBookIdDelegate()).get();
            
            if(book.getStock()>0) {
            	book.setStock(book.getStock()-1);
                bookRepository.save(book);
                loanRepository.save(loan);
            }
            else {
            	resultMsg = Constants.MSG_BINDING_ERROR;
                resultFlag = false;
            }
            
            serviceResponse.setMessage(resultMsg);
            serviceResponse.setSuccess(resultFlag);
            serviceResponse.setData(null);

            return serviceResponse;
    }

    @Override
    public ServiceResponse delete(Integer id) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            Loan loan = findById(id)
                    .orElseThrow(() -> new EntidadNoEncontradaException(id.toString()));
            loanRepository.delete(loan);

            serviceResponse.setMessage(Constants.MSG_ELIMINADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(null);
            return serviceResponse;
    }

    @Override
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

	// metodos para obtener data como lista
    @Override
    @Transactional(readOnly = true)
    public Slice<Loan> getList(Integer page, Integer rows) {
            return loanRepository.findAll(PageRequest.of(page - 1, rows));
    }
	
    @Override
    @Transactional(readOnly = true)
    public Slice<Loan> getListByQ(String q, Pageable page) {
        if(!StringUtils.isBlank(q)) {
            return loanRepository.findAll(page);
        }
        else return loanRepository.findAll(page);
    }
	
    // metodos para DataTable
    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<Loan> findAll(DataTablesInput input) {
            return loanRepository.findAll(input);
    }

    @Override
    public ServiceResponse returnBook(Loan loan) {
            ServiceResponse serviceResponse = new ServiceResponse(false, Constants.MSG_EXCEPCION_ACCION);
            Loan savedLoan = findById(loan.getId())
                    .orElseThrow(() -> new EntidadNoEncontradaException(loan.getId().toString()));
            
            Book book = bookRepository.findById(loan.getBookIdDelegate()).get();
            
            book.setStock(book.getStock()+1);
            bookRepository.save(book);
            
            savedLoan.setLentTo(loan.getLentTo());
            loanRepository.save(savedLoan);
            
            serviceResponse.setMessage(Constants.MSG_GUARDADO_EXITOSO);
            serviceResponse.setSuccess(true);
            serviceResponse.setData(savedLoan);

            return serviceResponse;
    }
    
}
