package com.focusservices.library.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import com.focusservices.library.commons.datatables.repository.DataTablesRepository;
import com.focusservices.library.domain.Loan;

//@JaversSpringDataAuditable
public interface LoanRepository extends DataTablesRepository<Loan, Integer> {
   

}
