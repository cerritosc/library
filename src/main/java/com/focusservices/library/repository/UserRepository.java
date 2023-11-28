package com.focusservices.library.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import com.focusservices.library.commons.datatables.repository.DataTablesRepository;
import com.focusservices.library.domain.User;

//@JaversSpringDataAuditable
public interface UserRepository extends DataTablesRepository<User, Integer> {
   

        Slice<User> findByFirstNameIgnoreCaseContaining(String q, Pageable page);
        
        Optional<User> findByEmail(String email);
}
